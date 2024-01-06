package com.oodesigns.ai.cloud;

import com.oodesigns.ai.general.Retrievable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CloudLoad<T> implements CloudLoadable<T> {
    private static final Logger logger = Logger.getLogger(CloudLoad.class.getName());
    private static final String UNABLE_TO_LOAD_FILE = "Unable to load file from store: %s and object: %s";
    private final Retrievable<CloudObjectReference, Optional<InputStream>> fileLoader;

    public CloudLoad(final Retrievable<CloudObjectReference, Optional<InputStream>> fileLoader) {
        this.fileLoader = fileLoader;
    }

    private IOException throwUnableToLoadFile(final CloudObjectReference cloudObjectReference) {
        return new IOException(String.format(UNABLE_TO_LOAD_FILE,
                cloudObjectReference.getStoreName(), cloudObjectReference.getObjectName()));
    }

    @Override
    public Optional<T> retrieve(final CloudObjectReference cloudObjectReference,
                                final CloudFunctionWithIOException<T> transformFunction) {
        try(final InputStream filestream =
                    fileLoader.retrieve(cloudObjectReference)
                            .orElseThrow(() -> throwUnableToLoadFile(cloudObjectReference))){
            return Optional.of(transformFunction.apply(filestream));
        } catch (final IOException e) {
            logger.log(Level.SEVERE,e.getMessage(),e);
            return Optional.empty();
        }
    }
}
