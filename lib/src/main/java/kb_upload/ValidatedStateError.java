package kb_upload;

import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class ValidatedStateError extends ValidationResult{

    private static final String VALIDATION_STATE_ERROR = "Validation State Error: %s";

    public ValidatedStateError(final List<String> messages) {
        this(messages.toString());
    }

    public ValidatedStateError(final String message) {
        super(String.format(VALIDATION_STATE_ERROR,message));
    }

    @Override
    public ValidationResult orElseThrow(final Function<ValidationResult, RuntimeException> functionException) throws RuntimeException {
        throw functionException.apply(this);
    }

    @Override
    public ValidationResult calling(final UnaryOperator<ValidationResult> function) {
        return this;
    }
}
