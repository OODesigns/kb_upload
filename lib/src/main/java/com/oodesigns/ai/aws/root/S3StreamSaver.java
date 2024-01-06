package com.oodesigns.ai.aws.root;
import com.oodesigns.ai.cloud.*;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class S3StreamSaver extends S3ClientSupplier implements CloudStorable {

    public S3StreamSaver(final S3Client s3Client) {
        super(s3Client);
    }

    private CloudStoreResult saveContents(final PutObjectRequest putRequest, final ByteArrayOutputStream contents) {
        // Do not close s3Client as it can be used across multiple invocations
        try(contents) {
            s3Client.putObject(putRequest, RequestBody.fromBytes(contents.toByteArray()));
            return new CloudStoreStateOK();
        }catch (final SdkException | IOException e) {
            return new CloudStoreStateError(e.toString());
        }
    }

    @Override
    public CloudStoreResult store(final CloudObjectReference cloudObjectReference, final ByteArrayOutputStream contents) {
        return saveContents(getPutRequest(cloudObjectReference), contents);
    }

    private PutObjectRequest getPutRequest(final CloudObjectReference cloudObjectReference) {
        return PutObjectRequest.builder()
                .bucket(cloudObjectReference.getStoreName())
                .key(cloudObjectReference.getObjectName())
                .build();
    }
}

