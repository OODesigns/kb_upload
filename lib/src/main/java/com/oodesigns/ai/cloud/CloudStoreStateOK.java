package com.oodesigns.ai.cloud;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class CloudStoreStateOK extends CloudStoreResult {
    public CloudStoreStateOK() {
        super("Store state OK");
    }

    @Override
    public CloudStoreStateOK orElseThrow(final Function<CloudStoreResult, RuntimeException> functionException) throws RuntimeException {
        return this;
    }

    @Override
    public CloudStoreResult calling(final UnaryOperator<CloudStoreResult> function) {
        return function.apply(this);
    }
}
