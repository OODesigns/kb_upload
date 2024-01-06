package com.oodesigns.ai.aws.root;

import com.oodesigns.ai.cloud.CloudObjectReference;
import com.oodesigns.ai.general.Retrievable;
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

public class S3StreamLoader extends S3ClientSupplier implements Retrievable<CloudObjectReference, Optional<InputStream>> {

    public S3StreamLoader(final S3Client s3Client) {
        super(s3Client);
    }

    @Override
    public Optional<InputStream> retrieve(final CloudObjectReference cloudObjectReference) {
        // Do not close s3Client as it can be used across multiple invocations
        try {
            return Optional.of(getGetRequest(cloudObjectReference))
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

    private GetObjectRequest getGetRequest(final CloudObjectReference cloudObjectReference) {
        return GetObjectRequest.builder()
                .bucket(cloudObjectReference.getStoreName())
                .key(cloudObjectReference.getObjectName())
                .build();
    }
}
