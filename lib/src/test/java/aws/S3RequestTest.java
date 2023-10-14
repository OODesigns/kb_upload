package aws;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@MockitoSettings
class S3RequestTest {

    private S3Request s3Request;
    @BeforeEach
    public void setUp() {
        s3Request = new S3Request();
    }

    @Test
    public void testGetPutRequest(final @Mock S3Object s3Object) {

        when(s3Object.getKeyName()).thenReturn("test-key");
        when(s3Object.getBucketName()).thenReturn("test-bucket");

        final PutObjectRequest request = s3Request.getPutRequest(s3Object);

        assertEquals("test-bucket", request.bucket());
        assertEquals("test-key", request.key());
    }

    @Test
    public void testGetGetRequest(final @Mock S3Object s3Object) {

        when(s3Object.getKeyName()).thenReturn("test-key");
        when(s3Object.getBucketName()).thenReturn("test-bucket");

        final GetObjectRequest request = s3Request.getGetRequest(s3Object);

        assertEquals("test-bucket", request.bucket());
        assertEquals("test-key", request.key());
    }

}