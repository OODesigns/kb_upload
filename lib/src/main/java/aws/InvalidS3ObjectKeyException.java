package aws;

public class InvalidS3ObjectKeyException extends RuntimeException {
    public InvalidS3ObjectKeyException(final String message) {
        super(message);
    }
}
