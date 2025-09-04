package com.oodesigns.ai.json;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JSONSchemaException extends RuntimeException {
    private static final Logger logger = Logger.getLogger(JSONSchemaException.class.getName());
    public JSONSchemaException(final String message) {
        super(message);
        logger.log(Level.SEVERE, message);
    }
}
