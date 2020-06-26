package id.renner.json.level;

public class TopLevel implements Level {

    @Override
    public LevelState getState() {
        return LevelState.START;
    }

    @Override
    public StringBuilder getBuffer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasContent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cleanUpAfterContentAdded() {
        throw new UnsupportedOperationException();
    }
}
