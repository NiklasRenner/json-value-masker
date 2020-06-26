package id.renner.json.level;

public class TopLevel implements Level {
    @Override
    public String getCurrentKeyName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCurrentKeyName(String keyName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LevelState getState() {
        return LevelState.START;
    }

    @Override
    public void revertState() {
        throw new UnsupportedOperationException();
    }

    @Override
    public StringBuilder getBuffer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void resetBuffer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isKeyAdded() {
        throw new UnsupportedOperationException();
    }
}
