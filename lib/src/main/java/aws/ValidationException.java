package aws;

import com.amazonaws.services.lambda.runtime.Context;

public class ValidationException extends RuntimeException {

    private static final String VALIDATION_EXCEPTION_ERROR_S = "ValidationException ERROR: %s";

    public ValidationException(final Context context, final String message) {
        super(message);
        context.getLogger().log(String.format(VALIDATION_EXCEPTION_ERROR_S, message));
    }
}
