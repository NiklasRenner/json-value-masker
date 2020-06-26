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

    public void handleNew(char character) {
        switch (character) {
            case '{' -> levelManager.enter(LevelState.OBJECT);
            case '[' -> levelManager.enter(LevelState.ARRAY);
            default -> throw new MalformedJsonException("expected start of json");
        }

        outputBuilder.append(character);
    }


    private void handleObject(ObjectLevel level, char character) {
        if (level.isKeyExpected()) {
            switch (character) {
                case '"' -> {
                    level.setKeyExpected(false);
                    level.setState(LevelState.KEY);
                }
                case ' ', '\r', '\n', '\t' -> {
                }
                case '}' -> {
                    if (level.isKeyAdded()) {
                        throw new MalformedJsonException("trailing comma");
                    }
                    exitLevel();
                }
                default -> throw new MalformedJsonException("expected key");
            }
        } else if (level.isValueExpected()) {
            switch (character) {
                case '{' -> levelManager.enter(LevelState.OBJECT);
                case '[' -> levelManager.enter(LevelState.ARRAY);
                case '"' -> level.setState(LevelState.VALUE);
                case ' ', '\r', '\n', '\t' -> {
                }
                default -> throw new MalformedJsonException("expected value");
            }
        } else {
            switch (character) {
                case ',' -> level.setKeyExpected(true);
                case ':' -> level.setValueExpected(true);
                case ' ', '\r', '\n', '\t' -> {
                }
                case '}' -> {
                    if (level.getCurrentKeyName() != null) {
                        throw new MalformedJsonException("");
                    }
                    exitLevel();
                }
                default -> throw new MalformedJsonException("unexpected character");
            }
        }

        outputBuilder.append(character);
    }

    private void handleArray(ArrayLevel level, char character) {
        if (level.isKeyExpected()) {
            switch (character) {
                case '{' -> levelManager.enter(LevelState.OBJECT);
                case '[' -> levelManager.enter(LevelState.ARRAY);
                case '"' -> {
                    level.setKeyExpected(false);
                    level.setState(LevelState.KEY);
                }
                case ' ', '\r', '\n', '\t' -> {
                }
                case ']' -> {
                    if (level.isKeyAdded()) {
                        throw new MalformedJsonException("trailing comma");
                    }
                    exitLevel();
                }
                default -> throw new MalformedJsonException("expected key");
            }
        } else {
            switch (character) {
                case ',' -> level.setKeyExpected(true);
                case ' ', '\r', '\n', '\t' -> {
                }
                case ']' -> exitLevel();
            }
        }

        outputBuilder.append(character);
    }

    private void handleValue(ObjectLevel level, char character) {
        if (character == '"') {
            var transformedValue = valueTransformer.transform(level, level.getBuffer().toString());
            outputBuilder.append(transformedValue);
            outputBuilder.append(character);
            level.setCurrentKeyName(null);
            level.setValueExpected(false);
            level.revertState();
            level.resetBuffer();
        } else {
            level.getBuffer().append(character);
        }
    }

    private void handleKey(Level level, char character) {
        if (character == '"') {
            var keyName = level.getBuffer().toString();
            level.setCurrentKeyName(keyName);
            outputBuilder.append(keyName);
            outputBuilder.append(character);
            level.revertState();
            level.resetBuffer();
        } else {
            level.getBuffer().append(character);
        }
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
