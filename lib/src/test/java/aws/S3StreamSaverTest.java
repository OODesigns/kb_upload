package aws;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings
class S3StreamSaverTest {
    @Test
    void savingReturnsOKState(@Mock final S3Client s3Client,
                              @Mock final S3RequestProvider s3RequestProvider,
                              @Mock final PutObjectRequest putObjectRequest,
                              @Mock final S3Object s3Object,
                              @Mock final ByteArrayOutputStream streamContents){

        when(s3RequestProvider.getPutRequest(any())).thenReturn(putObjectRequest);
        when(streamContents.toByteArray()).thenReturn("someData".getBytes());

        final S3StreamSaver s3StreamSaver = new S3StreamSaver(s3Client, s3RequestProvider);

        assertThat(s3StreamSaver.store(s3Object, streamContents)).isInstanceOf(S3FileSaverOKState.class);

        final ArgumentCaptor<RequestBody> contents = ArgumentCaptor.forClass(RequestBody.class);

        verify(s3Client,times(1)).putObject(any(PutObjectRequest.class), contents.capture());
        try (final InputStream stream = contents.getValue().contentStreamProvider().newStream()){

            final String expectedContents = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            assertThat(expectedContents).contains("someData");

        } catch (final IOException e) {
            fail("Unexpected failure");
        }
    }

    @Test
    void errorSavingReturnsErrorState(@Mock final S3Client s3Client,
                                      @Mock final S3RequestProvider s3RequestProvider,
                                      @Mock final S3ObjectFactory s3ObjectFactory,
                                      @Mock final PutObjectRequest putObjectRequest,
                                      @Mock final ByteArrayOutputStream streamContents){

        when(s3RequestProvider.getPutRequest(any())).thenReturn(putObjectRequest);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class) )).thenThrow(SdkException.class);
        when(streamContents.toByteArray()).thenReturn("someData".getBytes());

        final S3StreamSaver s3StreamSaver = new S3StreamSaver(s3Client, s3RequestProvider);

        assertThat(s3StreamSaver.store(s3ObjectFactory, streamContents)).isInstanceOf(S3FileSaverErrorState.class);

    }

}