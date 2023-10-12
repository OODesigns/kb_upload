package aws;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@MockitoSettings
class S3FileLoaderTest {

    @SuppressWarnings("unchecked")
    @Test
    void retrieveFileData(@Mock final S3Client s3Client,
                          @Mock final S3RequestProvider s3RequestProvider,
                          @Mock final ResponseBytes<GetObjectResponse> responseBytes,
                          @Mock final GetObjectRequest getObjectRequest){


        when(s3RequestProvider.getGetRequest(any())).thenReturn(getObjectRequest);
        when(responseBytes.asByteArray()).thenReturn("someData".getBytes(StandardCharsets.UTF_8));

        when(s3Client.getObject(any(GetObjectRequest.class), any(ResponseTransformer.class))).thenReturn(responseBytes);

        final S3FileLoader s3FileLoader = new S3FileLoader(()->s3Client, s3RequestProvider);

        s3FileLoader.retrieve(new S3ObjectName(new BucketName("expected-bucket"),new KeyName("expectedFilename.txt")))
                .ifPresentOrElse(s->assertThat(s).contains("someData"),
                        ()->fail("Expected to have some data"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void errorWhenGettingFileData(@Mock final S3Client s3Client,
                                  @Mock final S3RequestProvider s3RequestProvider,
                                  @Mock final GetObjectRequest getObjectRequest) {

        when(s3RequestProvider.getGetRequest(any())).thenReturn(getObjectRequest);
        when(s3Client.getObject(any(GetObjectRequest.class), any(ResponseTransformer.class))).thenThrow(SdkException.class);

        final S3FileLoader s3FileLoader = new S3FileLoader(()->s3Client, s3RequestProvider);

        final Optional<String> retrieve = s3FileLoader.retrieve(new S3ObjectName(new BucketName("expected-bucket"), new KeyName("expectedFilename.txt")));

        assertThat(retrieve).isEmpty();
    }
}