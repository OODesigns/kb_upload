package com.oodesigns.ai.aws.root;

import com.oodesigns.ai.cloud.CloudException;

public class InvalidBucketNameException extends CloudException {

    public InvalidBucketNameException(final String message) {
        super(message);
    }
}
