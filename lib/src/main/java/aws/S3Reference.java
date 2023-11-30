package aws;

import com.amazonaws.services.lambda.runtime.Context;
import java.util.Map;

public class S3Reference implements S3ObjectReference {
    private static final String BUCKET_NAME_IS_MISSING = "Bucket name for %s file is missing";
    private static final String KEY_NAME_IS_MISSING = "Key name for %s file is missing";
    private final String bucketName;
    private final String keyName;


    public S3Reference(final Map<String, String> input, final Context context,
                       final String bucketNameKey, final String keyNameKey, final String area) {

        this.bucketName = getBucketNameProvider(bucketNameKey, area, input, context).get();
        this.keyName = getKeyNameProvider(keyNameKey, area, input, context).get();
    }

    private static <T> T getProviderFromInput(final Map<String, String> input,
                                       final String key,
                                       final Context context,
                                       final Constructor<T> constructor,
                                       final String errorMessage) {
        try {
            return constructor.create(input.get(key));
        } catch (final RuntimeException e) {
            throw new AWSS3Exception(context, errorMessage);
        }
    }

    private interface Constructor<T> {
        T create(String input) throws RuntimeException;
    }

    private static BucketNameProvider getBucketNameProvider(final String key,
                                                            final String area,
                                                            final Map<String, String> input,
                                                            final Context context) {
        return getProviderFromInput(
                input,
                key,
                context,
                BucketName::new,
                String.format(BUCKET_NAME_IS_MISSING,area)
        );
    }

    private static KeyNameProvider getKeyNameProvider(final String key,
                                                      final String area,
                                                      final Map<String, String> input,
                                                      final Context context) {
        return getProviderFromInput(
                input,
                key,
                context,
                KeyName::new,
                String.format(KEY_NAME_IS_MISSING, area)
        );
    }

    @Override
    public String getBucketName() {
        return bucketName;
    }

    @Override
    public String getKeyName() {
        return keyName;
    }
}
