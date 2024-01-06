package com.oodesigns.ai.json;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class JSONValidatedResultStateOK extends JSONValidationResult {
    public JSONValidatedResultStateOK() {
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
