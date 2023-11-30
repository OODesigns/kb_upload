package aws;

import kb_upload.Retrievable;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Function;

public class S3StreamLoader extends S3ClientSupplier implements Retrievable<S3ObjectReference, Optional<InputStream>> {

    public S3StreamLoader(final S3Client s3Client) {
        super(s3Client);
    }

    @Override
    public Optional<InputStream> retrieve(final S3ObjectReference s3ObjectReference) {
        // Do not close s3Client as it can be used across multiple invocations
        try {
            return Optional.of(getGetRequest(s3ObjectReference))
                    .map(s3Client::getObject)
                    .flatMap(getResponse())
                    .map(ByteArrayInputStream::new);
        } catch (final SdkException e) {
            return Optional.empty();
        }
    }

    private Function<ResponseInputStream<GetObjectResponse>, Optional<byte[]>> getResponse() {
        return r -> {
            try {
                return Optional.of(r.readAllBytes());
            } catch (final IOException e) {
                return Optional.empty();
            }
        };
    }

    private GetObjectRequest getGetRequest(final S3ObjectReference s3ObjectReference) {
        return GetObjectRequest.builder()
                .bucket(s3ObjectReference.getBucketName())
                .key(s3ObjectReference.getKeyName())
                .build();
    }
}
