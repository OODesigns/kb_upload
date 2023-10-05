package aws;

import java.util.regex.Pattern;
import java.util.stream.Stream;

public class BucketName implements BucketNameProvider {
    private static final int MIN_NAME_LENGTH = 3;
    private static final int MAX_NAME_LENGTH = 63;
    private static final String INVALID_NAME = "Invalid AWS bucket name: ";
    private final String bucketName;

    public BucketName(final String bucketName) {
        this.bucketName = bucketName;
        if (!isValidBucketName(bucketName)) {
            throw new InvalidBucketNameException(INVALID_NAME + bucketName);
        }
    }

    private boolean isValidBucketName(final String bucketName) {
        return Stream.of(
                hasValidNameSize(bucketName),
                includesValidCharacters(bucketName),
                excludesIPAddressFormat(bucketName)
        ).allMatch(Boolean::booleanValue);
    }

    private static boolean excludesIPAddressFormat(final String bucketName) {
        return !Pattern.compile("(?:\\d{1,3}\\.){3}\\d{1,3}").matcher(bucketName).matches();
    }

    private static boolean includesValidCharacters(final String bucketName) {
        //contain only lowercase letters, numbers, hyphens, and dots.
        return bucketName.matches("^[a-z0-9][a-z0-9.-]*[a-z0-9]$");
    }

    private static boolean hasValidNameSize(final String bucketName) {
        return bucketName.length() >= MIN_NAME_LENGTH && bucketName.length() <= MAX_NAME_LENGTH;
    }

    @Override
    public String get() {
        return bucketName;
    }
}
