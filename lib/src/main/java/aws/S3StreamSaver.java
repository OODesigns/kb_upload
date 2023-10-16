package aws;

import kb_upload.Storable;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Supplier;

public class S3StreamSaver extends S3RequestSupplier implements Storable<S3Object, ByteArrayOutputStream, S3FileSaverState> {
    public S3StreamSaver(final Supplier<S3Client> s3Client, final S3RequestProvider s3RequestProvider) {
        super(s3Client, s3RequestProvider);
    }

    private S3FileSaverState saveContents(final PutObjectRequest putRequest, final ByteArrayOutputStream contents) {
        try(final S3Client client =  s3Client.get(); contents) {
            client.putObject(putRequest, RequestBody.fromBytes(contents.toByteArray()));
            return new S3FileSaverOKState();
        }catch (final SdkException | IOException e) {
            return new S3FileSaverErrorState(e.toString());
        }
    }

    @Override
    public S3FileSaverState store(final S3Object s3Object, final ByteArrayOutputStream contents) {
        return saveContents(getPutRequest(s3Object), contents);
    }
}
