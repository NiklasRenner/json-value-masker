package id.renner.json.level;

public class ArrayLevel implements Level {
    private final StringBuilder buffer;

    private LevelState state;
    private boolean keyExpected;
    private boolean keyAdded;

    public ArrayLevel() {
        this.buffer = new StringBuilder();

        this.state = LevelState.ARRAY;
        this.keyExpected = true;
        this.keyAdded = false;
    }

    public boolean isKeyExpected() {
        return keyExpected;
    }

    public void setKeyExpected(boolean keyExpected) {
        this.keyExpected = keyExpected;
    }

    public void setState(LevelState state) {
        this.state = state;
    }

    @Override
    public LevelState getState() {
        return state;
    }

    @Override
    public void revertState() {
        this.state = LevelState.ARRAY;
    }

    @Override
    public StringBuilder getBuffer() {
        return buffer;
    }

    @Override
    public String getCurrentKeyName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCurrentKeyName(String keyName) {
        keyAdded = true;
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
