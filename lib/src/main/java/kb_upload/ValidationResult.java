package kb_upload;
import java.util.function.Function;

public abstract class ValidationResult implements ValidatedState<ValidationResult>{
    private final String message;

    public ValidationResult(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public ValidationResult calling(final Function<ValidationResult, ValidationResult> function) {
        return function.apply(this);
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
