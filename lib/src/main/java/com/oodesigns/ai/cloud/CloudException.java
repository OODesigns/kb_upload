package com.oodesigns.ai.cloud;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CloudException extends RuntimeException{
    private static final Logger logger = Logger.getLogger(CloudException.class.getName());

    public CloudException(final String message) {
        super(message);
        logger.log(Level.SEVERE, message);
    }
}
