package com.oodesigns.ai.cloud;

import com.oodesigns.ai.general.ResultState;

public abstract class CloudStoreResult implements ResultState<CloudStoreResult,CloudStoreResult> {
    private final String message;

    protected CloudStoreResult(final String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
