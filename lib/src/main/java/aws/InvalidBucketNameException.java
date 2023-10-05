package aws;

public class InvalidBucketNameException extends RuntimeException {
    public InvalidBucketNameException(final String message) {
        super(message);
    }
}
