package aws;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@MockitoSettings
class S3StreamLoaderTest {

    @Test
    void testRetrieveSuccess(@Mock final S3Object s3Object,
                             @Mock final S3Client s3Client,
                             @Mock final S3RequestProvider s3RequestProvider,
                             @Mock final ResponseInputStream<GetObjectResponse> responseBytes,
                             @Mock final GetObjectRequest getObjectRequest
                             ) throws IOException {

        final byte[] testData = "Hello, World!".getBytes();

        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseBytes);
        when(responseBytes.readAllBytes()).thenReturn(testData);
        when(s3RequestProvider.getGetRequest(any())).thenReturn(getObjectRequest);

        // Act
        final S3StreamLoader s3StreamLoader = new S3StreamLoader(s3Client, s3RequestProvider);
        final Optional<InputStream> result = s3StreamLoader.retrieve(s3Object);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().readAllBytes()).isEqualTo(testData);
    }


    @Test
    void testRetrieveSdkException(
                             @Mock final S3Object s3Object,
                             @Mock final S3Client s3Client,
                             @Mock final S3RequestProvider s3RequestProvider,
                             @Mock final GetObjectRequest getObjectRequest){

        when(s3Client.getObject(any(GetObjectRequest.class))).thenThrow(SdkException.class);

        when(s3RequestProvider.getGetRequest(any())).thenReturn(getObjectRequest);

        // Act
        final S3StreamLoader s3StreamLoader = new S3StreamLoader(s3Client, s3RequestProvider);
        final Optional<InputStream> result = s3StreamLoader.retrieve(s3Object);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void testRetrieveIOException(
            @Mock final S3Object s3Object,
            @Mock final S3Client s3Client,
            @Mock final S3RequestProvider s3RequestProvider,
            @Mock final GetObjectRequest getObjectRequest,
            @Mock final ResponseInputStream<GetObjectResponse> objectResponseResponseInputStream) throws IOException {

        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(objectResponseResponseInputStream);
        when(objectResponseResponseInputStream.readAllBytes()).thenThrow(IOException.class);

        when(s3RequestProvider.getGetRequest(any())).thenReturn(getObjectRequest);

        // Act
        final S3StreamLoader s3StreamLoader = new S3StreamLoader(s3Client, s3RequestProvider);
        final Optional<InputStream> result = s3StreamLoader.retrieve(s3Object);

        // Assert
        assertThat(result).isEmpty();
    }
}