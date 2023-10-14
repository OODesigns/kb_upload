package aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class S3ObjectFactoryTest {

    private Map<String, String> input;
    private Context context;

    @BeforeEach
    public void setup() {
        input = new HashMap<>();
        context = mock(Context.class);
        when(context.getLogger()).thenReturn(mock(LambdaLogger.class));
    }

    @Test
    public void testValidBucketNameAndKeyName() {
        input.put("bucketNameKey", "test-bucket");
        input.put("keyNameKey", "test-key");

        final S3ObjectFactory factory = new S3ObjectFactory(input, context, "bucketNameKey", "keyNameKey", "testArea");

        assertEquals("test-bucket", factory.getBucketName());
        assertEquals("test-key", factory.getKeyName());
    }

    @Test
    public void testMissingBucketName() {
        input.put("keyNameKey", "test-key");

        assertThrows(s3Exception.class, () ->
                new S3ObjectFactory(input, context, "bucketNameKey", "keyNameKey", "testArea")
        );
    }

    @Test
    public void testMissingKeyName() {
        input.put("bucketNameKey", "test-bucket");

        assertThrows(s3Exception.class, () ->
                new S3ObjectFactory(input, context, "bucketNameKey", "keyNameKey", "testArea")
        );
    }
}