package json;

import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class JSONValidatedStateError extends JSONValidationResult {

    private static final String VALIDATION_STATE_ERROR = "Validation State Error: %s";

    public JSONValidatedStateError(final List<String> messages) {
        this(messages.toString());
    }

    public JSONValidatedStateError(final String message) {
        super(String.format(VALIDATION_STATE_ERROR,message));
    }

    @Override
    public JSONValidationResult orElseThrow(final Function<JSONValidationResult, RuntimeException> functionException) throws RuntimeException {
        throw functionException.apply(this);
    }

    @Override
    public JSONValidationResult calling(final UnaryOperator<JSONValidationResult> function) {
        return this;
    }
}
