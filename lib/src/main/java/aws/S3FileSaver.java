package aws;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class S3FileSaver {
    private final Supplier<S3Client> s3Client;

    public S3FileSaver(final Supplier<S3Client> s3Client) {
        this.s3Client = s3Client;
    }

    public Optional<S3FileSaverState> save(final BucketNameProvider bucketNameProvider, final KeyNameProvider keyNameProvider, final String contents) {
        return getRequest(bucketNameProvider, keyNameProvider)
                .map(saveContents(contents));
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

    private Optional<PutObjectRequest> getRequest(final BucketNameProvider bucketNameProvider, final KeyNameProvider keyNameProvider) {
        return Optional.of(PutObjectRequest.builder().bucket(bucketNameProvider.get()).key(keyNameProvider.get()).build());
    }
}
