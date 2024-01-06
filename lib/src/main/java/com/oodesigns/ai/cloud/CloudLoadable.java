package com.oodesigns.ai.cloud;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public interface CloudLoadable<T> {

    @FunctionalInterface
    interface CloudFunctionWithIOException<T> {
        T apply(InputStream t) throws IOException;
    }

    Optional<T> retrieve(CloudObjectReference cloudObjectReference, CloudFunctionWithIOException<T> transformFunction);
}
