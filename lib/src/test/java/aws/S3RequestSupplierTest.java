package aws;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
@MockitoSettings
class S3RequestSupplierTest {
    @Test
    public void testGetPutRequest(@Mock final S3Client S3client,
                                  @Mock final S3RequestProvider s3RequestProvider) {
        final S3Object mockS3Object = new S3ObjectName(new BucketName("testBucket"), new KeyName("testKey"));

        final S3RequestSupplier s3RequestSupplier = new S3RequestSupplier(() -> S3client, s3RequestProvider);

        final PutObjectRequest putRequest = PutObjectRequest.builder().bucket("testBucket").key("testKey").build();
        when(s3RequestProvider.getPutRequest(mockS3Object)).thenReturn(putRequest);

        final PutObjectRequest requestFromSupplier = s3RequestSupplier.getPutRequest(mockS3Object);
        assertEquals(putRequest, requestFromSupplier);
    }

    @Test
    public void testGetGetRequest(@Mock final S3Client S3client,
                                  @Mock final S3RequestProvider s3RequestProvider) {

        final S3Object mockS3Object = new S3ObjectName(new BucketName("testBucket"), new KeyName("testKey"));
        final GetObjectRequest getRequest = GetObjectRequest.builder().bucket("testBucket").key("testKey").build();

        final S3RequestSupplier s3RequestSupplier = new S3RequestSupplier(() -> S3client, s3RequestProvider);

        when(s3RequestProvider.getGetRequest(mockS3Object)).thenReturn(getRequest);

        final GetObjectRequest requestFromSupplier = s3RequestSupplier.getGetRequest(mockS3Object);
        assertEquals(getRequest, requestFromSupplier);
    }

}