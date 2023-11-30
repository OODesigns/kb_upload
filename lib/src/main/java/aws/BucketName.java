package aws;

import java.util.Optional;
import java.util.regex.Pattern;


public class BucketName implements BucketNameSupplier {
    private static final int MIN_NAME_LENGTH = 3;
    private static final int MAX_NAME_LENGTH = 63;
    private static final String INVALID_NAME = "Invalid AWS bucket name: %s";
    private final String bucketName;

    public BucketName(final String bucketName) {
        this.bucketName = isValidBucketName(bucketName)
                .orElseThrow(()->new InvalidBucketNameException(String.format(INVALID_NAME,bucketName)));
    }

    private Optional<String> isValidBucketName(final String bucketName) {
        return  hasValidNameSize(bucketName) &&
                includesValidCharacters(bucketName) &&
                excludesIPAddressFormat(bucketName) ? Optional.of(bucketName): Optional.empty();
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
