package com.oodesigns.ai.aws.root;

import com.oodesigns.ai.cloud.CloudException;

public class InvalidS3ObjectKeyException extends CloudException {

    public InvalidS3ObjectKeyException(final String message) {
        super(message);
    }
}
