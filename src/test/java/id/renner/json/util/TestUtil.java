package id.renner.json.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class TestUtil {

    public static String readFile(String fileName) {
        try {
            return Files.readString(Paths.get("src/test/resources/" + fileName));
        } catch (IOException ex) {
            throw new RuntimeException("file doesn't exist", ex);
        }
    }

    public static long time(Runnable runnable) {
        var before = System.currentTimeMillis();
        runnable.run();
        return System.currentTimeMillis() - before;
    }
}
