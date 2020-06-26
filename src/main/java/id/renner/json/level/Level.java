package id.renner.json.level;

public interface Level {

    LevelState getState();

    StringBuilder getBuffer();

    boolean hasContent();

    void cleanUpAfterContentAdded();
}
