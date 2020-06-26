package id.renner.json.level;

public class ArrayLevel implements Level {
    private final StringBuilder buffer;

    private LevelState state;
    private boolean valueExpected;
    private boolean contentAdded;

    public ArrayLevel() {
        this.buffer = new StringBuilder();

        this.state = LevelState.ARRAY;
        this.valueExpected = true;
        this.contentAdded = false;
    }

    public boolean isValueExpected() {
        return valueExpected;
    }

    public void setValueExpected(boolean valueExpected) {
        this.valueExpected = valueExpected;
    }

    public void setState(LevelState state) {
        this.state = state;
    }

    @Override
    public LevelState getState() {
        return state;
    }

    @Override
    public StringBuilder getBuffer() {
        return buffer;
    }

    @Override
    public boolean hasContent() {
        return contentAdded;
    }

    @Override
    public void cleanUpAfterContentAdded() {
        state = LevelState.ARRAY;
        buffer.setLength(0);
        valueExpected = false;
        contentAdded = true;
    }
}
