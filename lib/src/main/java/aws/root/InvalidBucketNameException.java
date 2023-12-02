package aws.root;

import java.util.logging.Level;
import java.util.logging.Logger;

public class InvalidBucketNameException extends RuntimeException {
    private static final Logger logger = Logger.getLogger(InvalidBucketNameException.class.getName());
    public InvalidBucketNameException(final String message) {
        super(message);
        logger.log(Level.SEVERE, message);
    }
}
