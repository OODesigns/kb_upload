package com.oodesigns.ai.cloud;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class CloudStoreStateError extends CloudStoreResult {
    public CloudStoreStateError(final String message) {
        super(message);
    }
    @Override
    public CloudStoreResult orElseThrow(final Function<CloudStoreResult, RuntimeException> functionException) throws RuntimeException {
        throw functionException.apply(this);
    }
    @Override
    public CloudStoreResult calling(final UnaryOperator<CloudStoreResult> function) {
        return this;
    }
}


