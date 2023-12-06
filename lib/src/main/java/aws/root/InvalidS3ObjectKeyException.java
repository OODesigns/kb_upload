package aws.root;

import cloud.CloudException;

public class InvalidS3ObjectKeyException extends CloudException {

    public InvalidS3ObjectKeyException(final String message) {
        super(message);
    }
}
