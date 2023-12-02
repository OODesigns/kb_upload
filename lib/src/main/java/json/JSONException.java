package json;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JSONException extends RuntimeException {
    private static final Logger logger = Logger.getLogger(JSONException.class.getName());
    public JSONException(final String message) {
        super(message);
        logger.log(Level.SEVERE, message);
    }
}
