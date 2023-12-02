package json;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class JSONValidatedStateOK extends JSONValidationResult {
    public JSONValidatedStateOK() {
        super("JSON Validation State OK");
    }

    @Override
    public JSONValidationResult orElseThrow(final Function<JSONValidationResult, RuntimeException> functionException) throws RuntimeException {
        return this;
    }

    @Override
    public JSONValidationResult calling(final UnaryOperator<JSONValidationResult> function) {
        return function.apply(this);
    }

}
