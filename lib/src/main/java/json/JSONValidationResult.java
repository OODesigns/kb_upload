package json;

import general.ResultState;

public abstract class JSONValidationResult implements ResultState<JSONValidationResult,JSONValidationResult> {
    private final String message;

    protected JSONValidationResult(final String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
