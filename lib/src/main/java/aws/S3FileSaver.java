package aws;
import kb_upload.Storable;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class S3FileSaver implements Storable<S3Object, String, Optional<S3FileSaverState>> {
    private final Supplier<S3Client> s3Client;

    public S3FileSaver(final Supplier<S3Client> s3Client) {
        this.s3Client = s3Client;
    }

    private Function<PutObjectRequest, S3FileSaverState> saveContents(final String contents) {
        return putObjectRequest -> {
            try {
                s3Client.get().putObject(putObjectRequest, RequestBody.fromString(contents));
                return new S3FileSaverOKState();
            }catch (final SdkException e) {
              return new S3FileSaverErrorState(e.toString());
            }
        };
    }

    private Optional<PutObjectRequest> getRequest(final S3Object s3object) {
        return Optional.of(PutObjectRequest.builder()
                .bucket(s3object.getBucketName())
                .key(s3object.getKeyName())
                .build());
    }

    @Override
    public Optional<S3FileSaverState> store(final S3Object s3Object, final String contents) {
        return getRequest(s3Object)
                .map(saveContents(contents));
    }
}
