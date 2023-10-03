package aws;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@MockitoSettings
public class S3FileSaverTest {

    public static final String EXPECTED_STATE_BUT_GOT_NOTHING = "Expected FileSaver State but got nothing";

    @Test
    void errorSavingReturnsErrorState(@Mock final S3Client s3Client,
                                      @Mock final BucketName bucketName,
                                      @Mock final Key key){

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class) )).thenThrow(SdkException.class);

        final S3FileSaver s3FileSaver = new S3FileSaver(() -> s3Client);

        s3FileSaver.save(bucketName, key, "someData")
                .ifPresentOrElse(s->assertThat(s).isInstanceOf(S3FileSaverErrorState.class),
                        ()->fail(EXPECTED_STATE_BUT_GOT_NOTHING));
    }
}
