package aws.root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class S3ReferenceTest {
    private Map<String, String> input;

    @BeforeEach
    public void setup() {
        input = new HashMap<>();
    }

    @Test
    public void testValidBucketNameAndKeyName() {
        input.put("bucketNameKey", "test-bucket");
        input.put("keyNameKey", "test-key");

        final S3CloudObjectReference factory = new S3CloudObjectReference(input, "bucketNameKey", "keyNameKey", "testArea");

        assertEquals("test-bucket", factory.getStoreName());
        assertEquals("test-key", factory.getObjectName());
    }

    @Test
    public void testMissingBucketName() {
        input.put("keyNameKey", "test-key");

        assertThrows(AWSS3Exception.class, () ->
                new S3CloudObjectReference(input, "bucketNameKey", "keyNameKey", "testArea")
        );
    }

    @Test
    public void testMissingKeyName() {
        input.put("bucketNameKey", "test-bucket");

        assertThrows(AWSS3Exception.class, () ->
                new S3CloudObjectReference(input, "bucketNameKey", "keyNameKey", "testArea")
        );
    }
}