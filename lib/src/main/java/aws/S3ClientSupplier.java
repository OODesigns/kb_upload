package aws;

import software.amazon.awssdk.services.s3.S3Client;

import java.util.function.Supplier;

public class S3ClientSupplier {
    protected final Supplier<S3Client> s3Client;

    public S3ClientSupplier(final Supplier<S3Client> s3Client) {
        this.s3Client = s3Client;
    }
}
