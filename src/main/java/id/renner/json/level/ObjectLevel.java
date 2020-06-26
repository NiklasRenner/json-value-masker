package id.renner.json.level;

public class ObjectLevel implements Level {
    private final StringBuilder buffer;

    private LevelState state;
    private String currentKeyName;
    private boolean valueExpected;
    private boolean keyExpected;
    private boolean contentAdded;

    public ObjectLevel() {
        this.buffer = new StringBuilder();

        this.state = LevelState.OBJECT;
        this.currentKeyName = null;
        this.valueExpected = false;
        this.keyExpected = true;
        this.contentAdded = false;
    }

    @Override
    public StringBuilder getBuffer() {
        return buffer;
    }

    public void setCurrentKeyName(String currentKeyName) {
        this.currentKeyName = currentKeyName;
    }

    public boolean isValueExpected() {
        return valueExpected;
    }

    public void setValueExpected(boolean valueExpected) {
        this.valueExpected = valueExpected;
    }

    public boolean isKeyExpected() {
        return keyExpected;
    }

    public void setKeyExpected(boolean keyExpected) {
        this.keyExpected = keyExpected;
    }

    public String getCurrentKeyName() {
        return currentKeyName;
    }

    @Override
    public LevelState getState() {
        return state;
    }

    public void setState(LevelState state) {
        this.state = state;
    }

    @Override
    public boolean hasContent() {
        return contentAdded;
    }

    public void cleanUpAfterKeyAdded() {
        state = LevelState.OBJECT;
        buffer.setLength(0);
    }

    @Override
    public void cleanUpAfterContentAdded() {
        state = LevelState.OBJECT;
        buffer.setLength(0);
        keyExpected = false;
        valueExpected = false;
        currentKeyName = null;
        contentAdded = true;
    }
}
