package kb_upload;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class
JsonTransformationResultTest {

    @Test
    public void testMapWithEmptyList() {
        final JsonTransformationResult result = new JsonTransformationResult(Collections.emptyList());

        final Optional<String> mappedValue = result.map(values -> String.join(", ", values));

        assertFalse(mappedValue.isPresent());
    }

    @Test
    public void testMapWithNonEmptyList() {
        final JsonTransformationResult result = new JsonTransformationResult(Arrays.asList("a", "b", "c"));

        final Optional<String> mappedValue = result.map(values -> String.join(", ", values));

        assertTrue(mappedValue.isPresent());
        assertEquals("a, b, c", mappedValue.get());
    }

    @Test
    public void testToStringWithEmptyList() {
        final JsonTransformationResult result = new JsonTransformationResult(Collections.emptyList());

        assertEquals("[]", result.toString());
    }

    @Test
    public void testToStringWithNonEmptyList() {
        final JsonTransformationResult result = new JsonTransformationResult(Arrays.asList("a", "b", "c"));

        assertEquals("[a, b, c]", result.toString());
    }

}