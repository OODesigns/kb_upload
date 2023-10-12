package aws;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import static org.junit.jupiter.api.Assertions.*;

class S3RequestTest {

    private S3Request s3Request;
    private S3Object mockS3Object;

    @BeforeEach
    public void setUp() {
        s3Request = new S3Request();
        mockS3Object = new S3ObjectName(new BucketName("testbucket"), new KeyName("testKey"));
    }

    @Test
    public void testGetPutRequest() {
        final PutObjectRequest request = s3Request.getPutRequest(mockS3Object);
        assertEquals("testbucket", request.bucket());
        assertEquals("testKey", request.key());
    }

    @Test
    public void testGetGetRequest() {
        final GetObjectRequest request = s3Request.getGetRequest(mockS3Object);
        assertEquals("testbucket", request.bucket());
        assertEquals("testKey", request.key());
    }

}