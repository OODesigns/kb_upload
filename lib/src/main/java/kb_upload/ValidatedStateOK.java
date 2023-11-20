package kb_upload;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class ValidatedStateOK extends ValidationResult{
    public ValidatedStateOK() {
        super("Validation State OK");
    }

    @Override
    public ValidationResult orElseThrow(final Function<ValidationResult, RuntimeException> functionException) throws RuntimeException {
        return this;
    }

    @Override
    public ValidationResult calling(final UnaryOperator<ValidationResult> function) {
        return function.apply(this);
    }

}
