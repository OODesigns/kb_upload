package aws;

import com.amazonaws.services.lambda.runtime.Context;

public class TransformationException extends RuntimeException{

    private static final String EXCEPTION_ERROR = "TransformationException ERROR: %s";

    public TransformationException(final Context context, final String message) {
        super(message);
        context.getLogger().log(String.format(EXCEPTION_ERROR, message));
    }
}
