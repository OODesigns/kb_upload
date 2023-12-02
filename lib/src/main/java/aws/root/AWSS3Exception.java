package aws.root;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AWSS3Exception extends RuntimeException{
    private static final Logger logger = Logger.getLogger(AWSS3Exception.class.getName());

    public AWSS3Exception(final String message) {
        super(message);
        logger.log(Level.SEVERE, message);
    }
}
