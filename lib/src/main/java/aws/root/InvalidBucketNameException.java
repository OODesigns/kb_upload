package aws.root;

import cloud.CloudException;

public class InvalidBucketNameException extends CloudException {

    public InvalidBucketNameException(final String message) {
        super(message);
    }
}
