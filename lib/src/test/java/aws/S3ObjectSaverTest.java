package aws;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings
public class S3ObjectSaverTest {

    public static final String EXPECTED_STATE_BUT_GOT_NOTHING = "Expected FileSaver State but got nothing";

    @Test
    void errorSavingReturnsErrorState(@Mock final S3Client s3Client,
                                      @Mock final BucketNameTransformer bucketNameTransformer,
                                      @Mock final KeyNameTransformer keyNameTransformer){

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class) )).thenThrow(SdkException.class);

        final S3FileSaver s3FileSaver = new S3FileSaver(() -> s3Client);

        s3FileSaver.save(bucketNameTransformer, keyNameTransformer, "someData")
                .ifPresentOrElse(s->assertThat(s).isInstanceOf(S3FileSaverErrorState.class),
                        ()->fail(EXPECTED_STATE_BUT_GOT_NOTHING));
    }

    @Test
    void savingReturnsOKState(@Mock final S3Client s3Client,
                                      @Mock final BucketNameTransformer bucketNameTransformer,
                                      @Mock final KeyNameTransformer keyNameTransformer){

        final S3FileSaver s3FileSaver = new S3FileSaver(() -> s3Client);

        final ArgumentCaptor<RequestBody> contents = ArgumentCaptor.forClass(RequestBody.class);

        s3FileSaver.save(bucketNameTransformer, keyNameTransformer, "someData")
                .ifPresentOrElse(s->assertThat(s).isInstanceOf(S3FileSaverOKState.class),
                        ()->fail(EXPECTED_STATE_BUT_GOT_NOTHING));

        verify(s3Client,times(1)).putObject(any(PutObjectRequest.class), contents.capture());
        try (final InputStream stream = contents.getValue().contentStreamProvider().newStream()){

            final String expectedContents = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            assertThat(expectedContents).contains("someData");

        } catch (final IOException e) {
            fail("Unexpected failure");
        }
    }

}
