package aws;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BucketNameTest {

    @Test
    public void testValidBucketNames() {
        assertDoesNotThrow(() -> new BucketName("valid-bucket-name"));
        assertDoesNotThrow(() -> new BucketName("a.valid.bucket.name"));
        assertDoesNotThrow(() -> new BucketName("a11"));
        assertDoesNotThrow(() -> new BucketName("bucket12345"));
    }

    @Test
    public void testInvalidBucketLength() {
        assertThrows(InvalidBucketNameException.class,
                () -> new BucketName("ab")); // Too short
        assertThrows(InvalidBucketNameException.class,
                () -> new BucketName("a".repeat(64))); // Too long
    }

    @Test
    public void testInvalidBucketCharacters() {
        assertThrows(InvalidBucketNameException.class,
                () -> new BucketName("InvalidNameBecauseUpperCase"));
        assertThrows(InvalidBucketNameException.class,
                () -> new BucketName("invalid*char"));
    }

    @Test
    public void testInvalidBucketStartOrEnd() {
        assertThrows(InvalidBucketNameException.class,
                () -> new BucketName("-invalidStart"));
        assertThrows(InvalidBucketNameException.class,
                () -> new BucketName("invalidEnd-"));
    }

    @Test
    public void testInvalidBucketAsIPAddress() {
        assertThrows(InvalidBucketNameException.class,
                () -> new BucketName("192.168.1.1"));
    }

}