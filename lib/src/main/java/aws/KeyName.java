package aws;

import java.util.Optional;
import java.util.function.Predicate;

public class KeyName implements KeyNameTransformer {
    public static final String INVALID_OBJECT_KEY = "Invalid S3 Object Key: %s";
    private final String keyname;

    public KeyName(final String keyname) {

        this.keyname = isValidObjectKey(keyname)
                .orElseThrow(()->new InvalidS3ObjectKeyException(String.format(INVALID_OBJECT_KEY,keyname)));
    }

    private Optional<String> isValidObjectKey(final String keyname) {
        return Optional.ofNullable(keyname)
                .filter(isNotEmpty())
                .filter(hasValidLength());
    }

//    Amazon S3 permits zero-length object keys
//    (i.e., empty strings) for object names. However, using a zero-length string as an
//    object key is typically not recommended for practical usage.
    private static Predicate<String> isNotEmpty() {
        return key -> !key.isEmpty();
    }

    private static Predicate<String> hasValidLength() {
        return key -> key.getBytes(java.nio.charset.StandardCharsets.UTF_8).length <= 1024;
    }

    @Override
    public String get() {
        return keyname;
    }
}
