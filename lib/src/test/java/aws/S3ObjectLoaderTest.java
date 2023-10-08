package aws;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@MockitoSettings
class S3ObjectLoaderTest {

    @Test
    void retrieveFileData(@Mock final AmazonS3 amazonS3){

        when(amazonS3.getObjectAsString(any(), any())).thenReturn("someData");

        final S3FileLoader s3FileLoader = new S3FileLoader(()->amazonS3);

        s3FileLoader.retrieve(new S3ObjectName(new BucketName("expected-bucket"),new KeyName("expectedFilename.txt")))
                .ifPresentOrElse(s->assertThat(s).contains("someData"),
                        ()->fail("Expected to have some data"));
    }

    @Test
    void errorWhenGettingFileData(@Mock final AmazonS3 amazonS3) {

        when(amazonS3.getObjectAsString(any(), any())).thenThrow(SdkClientException.class);

        final S3FileLoader s3FileLoader = new S3FileLoader(() -> amazonS3);

        final Optional<String> retrieve = s3FileLoader.retrieve(new S3ObjectName(new BucketName("expected-bucket"), new KeyName("expectedFilename.txt")));

        assertThat(retrieve).isEmpty();
    }
}