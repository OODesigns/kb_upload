package aws;

import kb_upload.Storable;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class S3StreamSaver extends S3ClientSupplier implements Storable<S3ObjectReference, ByteArrayOutputStream, S3FileSaverState> {

    public S3StreamSaver(final S3Client s3Client) {
        super(s3Client);
    }

    private S3FileSaverState saveContents(final PutObjectRequest putRequest, final ByteArrayOutputStream contents) {
        // Do not close s3Client as it can be used across multiple invocations
        try(contents) {
            s3Client.putObject(putRequest, RequestBody.fromBytes(contents.toByteArray()));
            return new S3FileSaverOKState();
        }catch (final SdkException | IOException e) {
            return new S3FileSaverErrorState(e.toString());
        }
    }

    @Override
    public S3FileSaverState store(final S3ObjectReference s3ObjectReference, final ByteArrayOutputStream contents) {
        return saveContents(getPutRequest(s3ObjectReference), contents);
    }

    private PutObjectRequest getPutRequest(final S3ObjectReference s3ObjectReference) {
        return PutObjectRequest.builder()
                .bucket(s3ObjectReference.getBucketName())
                .key(s3ObjectReference.getKeyName())
                .build();
    }
}

