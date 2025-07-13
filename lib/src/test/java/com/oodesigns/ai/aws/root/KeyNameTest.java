package com.oodesigns.ai.aws.root;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KeyNameTest {

    @Test
    void validKeyName() {
        final String key = "valid/key/name.jpg";
        assertThat(key).contains(new KeyName(key).get());
    }

    @Test
    void emptyKeyNameShouldThrowException() {
        assertThrows(InvalidS3ObjectKeyException.class, () -> new KeyName(""));
    }

    @Test
    void nullKeyNameShouldThrowException() {
        assertThrows(InvalidS3ObjectKeyException.class, () -> new KeyName(null));
    }

    @Test
    void keyNameExceedingLengthShouldThrowException() {
        final String longKey = "a".repeat(1025); // creating a string with length of 1025
        assertThrows(InvalidS3ObjectKeyException.class, () -> new KeyName(longKey));
    }

}