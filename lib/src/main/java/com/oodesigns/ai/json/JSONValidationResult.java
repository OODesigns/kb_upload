package com.oodesigns.ai.json;

import com.oodesigns.ai.general.ResultState;

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
