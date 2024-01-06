package com.oodesigns.ai.json;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class
JSONArrayToListResultTest {

    @Test
    public void testMapWithEmptyList() {
        final JSONArrayToListResult result = new JSONArrayToListResult(Collections.emptyList());

        final Optional<String> mappedValue = result.map(values -> String.join(", ", values));

        assertFalse(mappedValue.isPresent());
    }

    @Test
    public void testMapWithNonEmptyList() {
        final JSONArrayToListResult result = new JSONArrayToListResult(Arrays.asList("a", "b", "c"));

        final Optional<String> mappedValue = result.map(values -> String.join(", ", values));

        assertTrue(mappedValue.isPresent());
        assertEquals("a, b, c", mappedValue.get());
    }

    @Test
    public void testToStringWithEmptyList() {
        final JSONArrayToListResult result = new JSONArrayToListResult(Collections.emptyList());

        assertEquals("[]", result.toString());
    }

    @Test
    public void testToStringWithNonEmptyList() {
        final JSONArrayToListResult result = new JSONArrayToListResult(Arrays.asList("a", "b", "c"));

        assertEquals("[a, b, c]", result.toString());
    }

}