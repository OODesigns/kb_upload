package aws;
import kb_upload.Retrievable;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Supplier;


public class S3FileLoader extends S3RequestSupplier implements Retrievable<S3Object, Optional<String>> {


    public S3FileLoader(final Supplier<S3Client> s3Client, final S3RequestProvider s3RequestProvider) {
        super(s3Client, s3RequestProvider);
    }

    @Override
    public Optional<String> retrieve(final S3Object s3Object) {
        try {
            return Optional.of(new String(getByteArray(s3Object), StandardCharsets.UTF_8));
        }catch (final SdkException e){
            return Optional.empty();
        }
    }

    private byte[] getByteArray(final S3Object s3Object) {
        try(final S3Client client = s3Client.get()) {
            return client.getObject(getGetRequest(s3Object), ResponseTransformer.toBytes()).asByteArray();
        }
    }
}
