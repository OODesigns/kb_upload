package aws;

import com.amazonaws.services.lambda.runtime.Context;

public class ValidationException extends RuntimeException {

    private static final String EXCEPTION_ERROR = "ValidationException ERROR: %s";

    public ValidationException(final Context context, final String message) {
        super(message);
        context.getLogger().log(String.format(EXCEPTION_ERROR, message));
    }
}
