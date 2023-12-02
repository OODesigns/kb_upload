package aws.root;

import java.util.logging.Level;
import java.util.logging.Logger;

public class InvalidS3ObjectKeyException extends RuntimeException {
    private static final Logger logger = Logger.getLogger(InvalidBucketNameException.class.getName());
    public InvalidS3ObjectKeyException(final String message) {
        super(message);
        logger.log(Level.SEVERE, message);
    }
}
