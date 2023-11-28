package aws;

import software.amazon.awssdk.services.s3.S3Client;

public class S3ClientSupplier {
    protected final S3Client s3Client;

    public S3ClientSupplier(final S3Client s3Client) {
        this.s3Client = s3Client;
    }
}
