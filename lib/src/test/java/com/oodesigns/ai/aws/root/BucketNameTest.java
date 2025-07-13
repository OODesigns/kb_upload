package com.oodesigns.ai.aws.root;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static software.amazon.awssdk.utils.StringUtils.repeat;

class BucketNameTest {

    @Test
    void testValidBucketNames() {
        assertDoesNotThrow(() -> new BucketName("valid-bucket-name"));
        assertDoesNotThrow(() -> new BucketName("a.valid.bucket.name"));
        assertDoesNotThrow(() -> new BucketName("a11"));
        assertDoesNotThrow(() -> new BucketName("bucket12345"));
    }

    @Test
    void testInvalidBucketLengthTooShort() {
        assertThrows(InvalidBucketNameException.class,
                () -> new BucketName("ab")); // Too short
    }

    @Test
    void testInvalidBucketLengthTooLong() {
        final String tooLong = repeat("a", 64);
        assertThrows(InvalidBucketNameException.class,
                () -> new BucketName(tooLong));
    }

    @Test
    void testInvalidBucketCharacters() {
        assertThrows(InvalidBucketNameException.class,
                () -> new BucketName("InvalidNameBecauseUpperCase"));
        assertThrows(InvalidBucketNameException.class,
                () -> new BucketName("invalid*char"));
    }

    @Test
    void testInvalidBucketStartOrEnd() {
        assertThrows(InvalidBucketNameException.class,
                () -> new BucketName("-invalidStart"));
        assertThrows(InvalidBucketNameException.class,
                () -> new BucketName("invalidEnd-"));
    }

    @Test
    void testInvalidBucketAsIPAddress() {
        assertThrows(InvalidBucketNameException.class,
                () -> new BucketName("192.168.1.1"));
    }

}