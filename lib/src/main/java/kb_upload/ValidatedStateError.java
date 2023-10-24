package kb_upload;

import aws.AWSS3Exception;

import java.util.List;
import java.util.function.Function;

public class ValidatedStateError extends ValidationResult{

    private static final String VALIDATION_STATE_ERROR = "Validation State Error: %s";

    public ValidatedStateError(final List<String> messages) {
        this(messages.toString());
    }

    public ValidatedStateError(final String message) {
        super(String.format(VALIDATION_STATE_ERROR,message));
    }

    @Override
    public ValidationResult orElseThrow(final Function<ValidationResult, AWSS3Exception> functionException) throws AWSS3Exception {
        throw functionException.apply(this);
    }
}
