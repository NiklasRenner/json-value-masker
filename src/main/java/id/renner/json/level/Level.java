package id.renner.json.level;

public interface Level {

    LevelState getState();

    void revertState();

    StringBuilder getBuffer();

    void resetBuffer();

    String getCurrentKeyName();

    void setCurrentKeyName(String keyName);

    boolean isKeyAdded();
}
