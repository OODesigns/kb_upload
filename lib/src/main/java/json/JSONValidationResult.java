package json;

public abstract class JSONValidationResult implements JSONValidatedState<JSONValidationResult> {
    private final String message;

    protected JSONValidationResult(final String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
