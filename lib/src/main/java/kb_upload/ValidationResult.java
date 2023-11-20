package kb_upload;

public abstract class ValidationResult implements ValidatedState<ValidationResult>{
    private final String message;

    protected ValidationResult(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
