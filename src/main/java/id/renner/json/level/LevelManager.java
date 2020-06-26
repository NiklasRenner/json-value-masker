package id.renner.json.level;

import id.renner.json.exception.MalformedJsonException;

import java.util.Stack;

public class LevelManager {
    private final Stack<Level> context;

    public LevelManager() {
        this.context = new Stack<>();
        this.context.push(new TopLevel());
    }

    public void exit() {
        context.pop();
    }

    public void enter(LevelState token) {
        switch (token) {
            case OBJECT -> context.push(new ObjectLevel());
            case ARRAY -> context.push(new ArrayLevel());
            default -> throw new MalformedJsonException("new level has to be array or object");
        }
    }

    public Level getCurrent() {
        return context.peek();
    }
}
