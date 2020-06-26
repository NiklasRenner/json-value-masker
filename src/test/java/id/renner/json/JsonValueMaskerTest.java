package id.renner.json;


import id.renner.json.exception.MalformedJsonException;
import id.renner.json.util.TestUtil;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonValueMaskerTest {
    private final JsonValueMasker masker = new JsonValueMasker();

    @Test
    public void testSimple() {
        var input = TestUtil.readFile("input/valid/simple.json");
        var expected = TestUtil.readFile("expected/simple.json");
        var maskedFields = Set.of("input");

        var inputStream = new ByteArrayInputStream(input.getBytes());
        var output = masker.mask(inputStream, maskedFields);

        assertEquals(expected, output);
    }

    @Test
    public void testObjectValue() {
        var input = TestUtil.readFile("input/valid/object-value.json");
        var expected = TestUtil.readFile("expected/object-value.json");
        var maskedFields = Set.of("input");

        var inputStream = new ByteArrayInputStream(input.getBytes());
        var output = masker.mask(inputStream, maskedFields);

        assertEquals(expected, output);
    }

    @Test
    public void testArrayValue() {
        var input = TestUtil.readFile("input/valid/array-value.json");
        var expected = TestUtil.readFile("expected/array-value.json");
        var maskedFields = Set.of("input");

        var inputStream = new ByteArrayInputStream(input.getBytes());
        var output = masker.mask(inputStream, maskedFields);

        assertEquals(expected, output);
    }

    @Test
    public void testComplex() {
        var input = TestUtil.readFile("input/valid/complex.json");
        var expected = TestUtil.readFile("expected/complex.json");
        var maskedFields = Set.of("abc", "input");

        var inputStream = new ByteArrayInputStream(input.getBytes());
        var output = masker.mask(inputStream, maskedFields);

        assertEquals(expected, output);
    }

    @Test
    public void testObjectNoValue() {
        var input = TestUtil.readFile("input/malformed/object-no-value.json");
        var maskedFields = Set.of("abc", "input");

        var inputStream = new ByteArrayInputStream(input.getBytes());
        assertThrows(MalformedJsonException.class, () -> {
            masker.mask(inputStream, maskedFields);
        });
    }

    @Test
    public void testTrailingComma() {
        var input = TestUtil.readFile("input/malformed/trailing-comma.json");
        var inputStream = new ByteArrayInputStream(input.getBytes());
        assertThrows(MalformedJsonException.class, () -> {
            masker.mask(inputStream, Collections.emptySet());
        });
    }

    @Test
    public void testObjectKeyOnly() {
        var input = TestUtil.readFile("input/malformed/object-key-only.json");
        var inputStream = new ByteArrayInputStream(input.getBytes());
        assertThrows(MalformedJsonException.class, () -> {
            masker.mask(inputStream, Collections.emptySet());
        });
    }
}