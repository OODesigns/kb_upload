package aws;

import kb_upload.Retrievable;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Function;

public class S3StreamLoader extends S3RequestSupplier implements Retrievable<S3Object, Optional<InputStream>> {


    public S3StreamLoader(final S3Client s3Client, final S3RequestProvider s3RequestProvider) {
        super(s3Client, s3RequestProvider);
    }

    @Override
    public Optional<InputStream> retrieve(final S3Object s3Object) {
        // Do not close s3Client as it can be used across multiple invocations
        try {
            return Optional.of(getGetRequest(s3Object))
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
}
