package id.renner.json;

import id.renner.json.exception.MalformedJsonException;
import id.renner.json.level.*;
import id.renner.json.transform.StringValueTransformer;

import java.io.IOException;
import java.io.InputStream;

public class StreamingJsonParser {
    private final InputStream inputStream;
    private final StringValueTransformer stringValueTransformer;
    private final LevelManager levelManager;
    private final StringBuilder outputBuilder;

    public StreamingJsonParser(InputStream inputStream, StringValueTransformer stringValueTransformer) {
        this.inputStream = inputStream;
        this.stringValueTransformer = stringValueTransformer;
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
                case KEY -> handleKey((ObjectLevel) level, character);
                case VALUE -> handleValue(level, character);
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
        switch (character) {
            case ' ', '\r', '\n', '\t' -> {
                outputBuilder.append(character);
                return;
            }
        }

        if (level.isKeyExpected()) { // before key
            switch (character) {
                case '"' -> {
                    level.setKeyExpected(false);
                    level.setState(LevelState.KEY);
                }
                case '}' -> {
                    if (level.hasContent()) {
                        throw new MalformedJsonException("trailing comma");
                    }
                    exitLevel();
                }
                default -> throw new MalformedJsonException("expected key");
            }
        } else if (level.isValueExpected()) { // before value
            switch (character) {
                case '{' -> levelManager.enter(LevelState.OBJECT);
                case '[' -> levelManager.enter(LevelState.ARRAY);
                case '"' -> level.setState(LevelState.VALUE);
                default -> throw new MalformedJsonException("expected value");
            }
        } else if (level.getCurrentKeyName() != null) { // between key and value
            switch (character) {
                case ':' -> level.setValueExpected(true);
                default -> throw new MalformedJsonException("expected key-value separator");
            }
        } else {
            switch (character) { // after value
                case ',' -> level.setKeyExpected(true);
                case '}' -> exitLevel();
                default -> throw new MalformedJsonException("expected end of object or separator");
            }
        }

        outputBuilder.append(character);
    }

    private void handleArray(ArrayLevel level, char character) {
        switch (character) {
            case ' ', '\r', '\n', '\t' -> {
                outputBuilder.append(character);
                return;
            }
        }

        if (level.isValueExpected()) { // before key
            switch (character) {
                case '{' -> levelManager.enter(LevelState.OBJECT);
                case '[' -> levelManager.enter(LevelState.ARRAY);
                case '"' -> {
                    level.setValueExpected(false);
                    level.setState(LevelState.VALUE);
                }
                case ']' -> {
                    if (level.hasContent()) {
                        throw new MalformedJsonException("trailing comma");
                    }
                    exitLevel();
                }
                default -> throw new MalformedJsonException("expected key");
            }
        } else { // after key
            switch (character) {
                case ',' -> level.setValueExpected(true);
                case ']' -> exitLevel();
                default -> throw new MalformedJsonException("expected end of array or value separator");
            }
        }

        outputBuilder.append(character);
    }

    private void handleValue(Level level, char character) {
        if (character == '"') {
            var transformedValue = stringValueTransformer.transform(level, level.getBuffer().toString());
            outputBuilder.append(transformedValue);
            outputBuilder.append(character);
            level.cleanUpAfterContentAdded();
        } else {
            level.getBuffer().append(character);
        }
    }

    private void handleKey(ObjectLevel level, char character) {
        if (character == '"') {
            var keyName = level.getBuffer().toString();
            level.setCurrentKeyName(keyName);
            outputBuilder.append(keyName);
            outputBuilder.append(character);
            level.cleanUpAfterKeyAdded();
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
                cast.setValueExpected(false);
            }
        }
    }
}
