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
                                  @Mock final S3RequestProvider s3RequestProvider,
                                  @Mock final S3Object s3Object) {

        final S3RequestSupplier s3RequestSupplier = new S3RequestSupplier(() -> S3client, s3RequestProvider);

        final PutObjectRequest putRequest = PutObjectRequest.builder().bucket("test-bucket").key("test-key").build();
        when(s3RequestProvider.getPutRequest(s3Object)).thenReturn(putRequest);

        final PutObjectRequest requestFromSupplier = s3RequestSupplier.getPutRequest(s3Object);
        assertEquals(putRequest, requestFromSupplier);
    }

    @Test
    public void testGetGetRequest(@Mock final S3Client S3client,
                                  @Mock final S3RequestProvider s3RequestProvider,
                                  @Mock final S3Object s3Object) {

        final GetObjectRequest getRequest = GetObjectRequest.builder().bucket("test-bucket").key("test-key").build();

        final S3RequestSupplier s3RequestSupplier = new S3RequestSupplier(() -> S3client, s3RequestProvider);

        when(s3RequestProvider.getGetRequest(s3Object)).thenReturn(getRequest);

        final GetObjectRequest requestFromSupplier = s3RequestSupplier.getGetRequest(s3Object);
        assertEquals(getRequest, requestFromSupplier);
    }

}