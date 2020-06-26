package id.renner.json.level;

public class ObjectLevel implements Level {
    private final StringBuilder buffer;

    private LevelState state;
    private String currentKeyName;
    private boolean valueExpected;
    private boolean keyExpected;
    private boolean keyAdded;

    public ObjectLevel() {
        this.buffer = new StringBuilder();

        this.state = LevelState.OBJECT;
        this.currentKeyName = null;
        this.valueExpected = false;
        this.keyExpected = true;
        this.keyAdded = false;
    }

    @Override
    public StringBuilder getBuffer() {
        return buffer;
    }

    @Override
    public void setCurrentKeyName(String currentKeyName) {
        this.currentKeyName = currentKeyName;
        this.keyAdded = true;
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

    @Override
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
    public void revertState() {
        state = LevelState.OBJECT;
    }

    @Override
    public void resetBuffer() {
        buffer.setLength(0);
    }

    @Override
    public boolean isKeyAdded() {
        return keyAdded;
    }
}
