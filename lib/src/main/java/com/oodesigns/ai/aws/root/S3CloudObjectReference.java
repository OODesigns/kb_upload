package com.oodesigns.ai.aws.root;

import com.oodesigns.ai.cloud.CloudException;
import com.oodesigns.ai.cloud.CloudObjectReference;

import java.util.Map;

public class S3CloudObjectReference implements CloudObjectReference {
    private static final String BUCKET_NAME_IS_MISSING = "Bucket name for %s file is missing";
    private static final String KEY_NAME_IS_MISSING = "Key name for %s file is missing";
    private final String bucketName;
    private final String keyName;


    public S3CloudObjectReference(final Map<String, String> input, final String bucketNameKey, final String keyNameKey, final String area) {

        this.bucketName = getBucketNameSupplier(bucketNameKey, area, input).get();
        this.keyName = getKeyNameSupplier(keyNameKey, area, input).get();
    }

    private static <T> T getSupplierFromInput(final Map<String, String> input,
                                              final String key,
                                              final Constructor<T> constructor,
                                              final String errorMessage) {
        try {
            return constructor.create(input.get(key));
        } catch (final RuntimeException e) {
            throw new CloudException(errorMessage);
        }
    }

    private interface Constructor<T> {
        T create(String input) throws RuntimeException;
    }

    private static BucketNameSupplier getBucketNameSupplier(final String key,
                                                            final String area,
                                                            final Map<String, String> input) {
        return getSupplierFromInput(
                input,
                key,
                BucketName::new,
                String.format(BUCKET_NAME_IS_MISSING,area)
        );
    }

    private static KeyNameSupplier getKeyNameSupplier(final String key,
                                                      final String area,
                                                      final Map<String, String> input) {
        return getSupplierFromInput(
                input,
                key,
                KeyName::new,
                String.format(KEY_NAME_IS_MISSING, area)
        );
    }

    @Override
    public String getStoreName() {
        return bucketName;
    }

    @Override
    public String getObjectName() {
        return keyName;
    }
}
