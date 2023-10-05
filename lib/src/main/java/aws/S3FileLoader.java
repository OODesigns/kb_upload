package aws;

import com.amazonaws.services.s3.AmazonS3;
import kb_upload.Retrievable;

import java.util.Optional;
import java.util.function.Supplier;

public class S3FileLoader implements Retrievable<S3File, Optional<String>> {
    private final Supplier<AmazonS3> amazonS3;

    public S3FileLoader(final Supplier<AmazonS3> amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Override
    public Optional<String> retrieve(final S3File s3File) {
        return Optional.empty();
    }
}
