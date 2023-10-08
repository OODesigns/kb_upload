package aws;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class S3ObjectTest {

    @Test
    void getBucketName() {

        final S3Object s3Object = new S3ObjectName(new BucketName("expected-bucket"), new KeyName("expectedFilename.txt"));

        assertThat(s3Object.getBucketName()).isEqualTo("expected-bucket");
    }

    @Test
    void getKeyName() {
        final S3Object s3Object = new S3ObjectName(new BucketName("expected-bucket"), new KeyName("expectedFilename.txt"));

        assertThat(s3Object.getKeyName()).isEqualTo("expectedFilename.txt");
    }
}