package aws;
import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@MockitoSettings
class S3FileLoaderTest {

    @Test
    void retrieveSingleFileData(@Mock final AmazonS3 amazonS3){

        when(amazonS3.getObjectAsString(any(), any())).thenReturn("someData");

        final S3FileLoader s3FileLoader = new S3FileLoader(()->amazonS3);

        s3FileLoader.retrieve(new S3File(new BucketName("expectedBucket"),new KeyName("expectedFilename.txt")))
                .ifPresentOrElse(s->assertThat(s).contains("someData"),
                        ()->fail("Expected to have some data"));
    }
}