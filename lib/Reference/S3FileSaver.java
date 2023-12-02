package aws;
import maker.Storable;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.util.function.Supplier;

public class S3FileSaver extends S3RequestSupplier implements Storable<S3Object, String, S3FileSaverState> {

    public S3FileSaver(final Supplier<S3Client> s3Client, final S3RequestProvider s3RequestProvider) {
        super(s3Client, s3RequestProvider);
    }

    private S3FileSaverState saveContents(final PutObjectRequest putObjectRequest, final String contents) {
           try(final S3Client client =  s3Client.get()) {
                client.putObject(putObjectRequest, RequestBody.fromString(contents));
                return new S3FileSaverOKState();
            }catch (final SdkException e) {
              return new S3FileSaverErrorState(e.toString());
            }
    }

    @Override
    public S3FileSaverState store(final S3Object s3Object, final String contents) {
        return saveContents(getPutRequest(s3Object), contents);
    }
}
