package function;

import software.amazon.awssdk.services.s3.S3Client;

import java.util.function.Supplier;

public class S3FileLoader {
    public S3FileLoader(final Supplier<S3Client> client, final String knowledgeJson) {
    }
}
