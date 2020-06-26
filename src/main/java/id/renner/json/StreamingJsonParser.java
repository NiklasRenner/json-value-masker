package id.renner.json;

import id.renner.json.exception.MalformedJsonException;
import id.renner.json.level.*;
import id.renner.json.transform.ValueTransformer;

import java.io.IOException;
import java.io.InputStream;

public class StreamingJsonParser {
    private final InputStream inputStream;
    private final ValueTransformer valueTransformer;
    private final LevelManager levelManager;
    private final StringBuilder outputBuilder;

    public StreamingJsonParser(InputStream inputStream, ValueTransformer valueTransformer) {
        this.inputStream = inputStream;
        this.valueTransformer = valueTransformer;
        this.outputBuilder = new StringBuilder();
        this.levelManager = new LevelManager();
    }

    public String parse() {
        try {
            read();
        } catch (IOException ex) {
            throw new RuntimeException("failed to read from inputstream", ex);
        }

        return outputBuilder.toString();
    }

    private void read() throws IOException {
        int value;
        while ((value = inputStream.read()) != -1) {
            var character = (char) value;
            var level = levelManager.getCurrent();
            switch (level.getState()) {
                case START -> handleNew(character);
                case OBJECT -> handleObject((ObjectLevel) level, character);
                case ARRAY -> handleArray((ArrayLevel) level, character);
                case KEY -> handleKey(level, character);
                case VALUE -> handleValue((ObjectLevel) level, character);
            }
        }
    }

    private void handleObject(ObjectLevel level, char character) {
        switch (character) {
            case '{':
                if (!level.isValueExpected()) {
                    throw new MalformedJsonException("unexpected start of object");
                }
                levelManager.enter(LevelState.OBJECT);
                break;
            case '[':
                if (!level.isValueExpected()) {
                    throw new MalformedJsonException("unexpected start of object");
                }
                levelManager.enter(LevelState.ARRAY);
                break;
            case ':':
                if (level.getCurrentKeyName() == null) {
                    throw new MalformedJsonException("value marker with no key before it");
                }
                if (level.isValueExpected()) {
                    throw new MalformedJsonException("redundant value marker");
                }
                level.setValueExpected(true);
                break;
            case '"':
                if (level.isValueExpected()) {
                    level.setState(LevelState.VALUE);
                } else {
                    if (!level.isKeyExpected()) {
                        throw new MalformedJsonException("new entry without separator");
                    }
                    level.setKeyExpected(false);
                    level.setState(LevelState.KEY);
                }
                break;
            case ',':
                if (level.isKeyExpected()) {
                    throw new MalformedJsonException("entry separator when none expected");
                }

                level.setKeyExpected(true);
                break;
            case ' ', '\r', '\n', '\t':
                break;
            case '}':
                if (level.getCurrentKeyName() != null || level.isValueExpected()) {
                    throw new MalformedJsonException("can't end object early");
                }
                if (level.isKeyAdded() && level.isKeyExpected()) {
                    throw new MalformedJsonException("trailing comma");
                }
                exitLevel();
                break;
            default:
                throw new MalformedJsonException("expected start of key, but got [ " + character + " ]");
        }

        outputBuilder.append(character);
    }

    private void handleArray(ArrayLevel level, char character) {
        switch (character) {
            case '{':
                if (!level.isKeyExpected()) {
                    throw new MalformedJsonException("new entry without separator");
                }
                levelManager.enter(LevelState.OBJECT);
                break;
            case '[':
                if (!level.isKeyExpected()) {
                    throw new MalformedJsonException("new entry without separator");
                }
                levelManager.enter(LevelState.ARRAY);
                break;
            case '"':
                if (!level.isKeyExpected()) {
                    throw new MalformedJsonException("new entry without separator");
                }
                level.setKeyExpected(false);
                level.setState(LevelState.KEY);
                break;
            case ',':
                if (level.isKeyExpected()) {
                    throw new MalformedJsonException("entry separator when none expected");
                }
                level.setKeyExpected(true);
                break;
            case ' ', '\r', '\n', '\t':
                break;
            case ']':
                if (level.isKeyAdded() && level.isKeyExpected()) {
                    throw new MalformedJsonException("trailing comma");
                }
                exitLevel();
                break;
            default:
                throw new MalformedJsonException("expected start of key, but got [ " + character + " ]");
        }

        outputBuilder.append(character);
    }

    private void handleValue(ObjectLevel level, char character) {
        if (character == '"') {
            level.revertState();
            var transformedValue = valueTransformer.transform(level, level.getBuffer().toString());
            level.resetBuffer();
            outputBuilder.append(transformedValue);
            outputBuilder.append(character);
            level.setCurrentKeyName(null);
            level.setValueExpected(false);
        } else {
            level.getBuffer().append(character);
        }
    }

    private void handleKey(Level level, char character) {
        if (character == '"') {
            level.revertState();
            var keyName = level.getBuffer().toString();
            level.resetBuffer();
            level.setCurrentKeyName(keyName);
            outputBuilder.append(keyName);
            outputBuilder.append(character);
        } else {
            level.getBuffer().append(character);
        }
    }

    public void handleNew(char character) {
        switch (character) {
            case '{' -> levelManager.enter(LevelState.OBJECT);
            case '[' -> levelManager.enter(LevelState.ARRAY);
            default -> throw new MalformedJsonException("start of json wasn't array or object");
        }

        outputBuilder.append(character);
    }

    private void exitLevel() {
        levelManager.exit();
        var current = levelManager.getCurrent();
        switch (current.getState()) {
            case OBJECT -> {
                var cast = (ObjectLevel) current;
                cast.setValueExpected(false);
                cast.setCurrentKeyName(null);
            }
            case ARRAY -> {
                var cast = (ArrayLevel) current;
                cast.setKeyExpected(false);
            }
        }
    }
}
