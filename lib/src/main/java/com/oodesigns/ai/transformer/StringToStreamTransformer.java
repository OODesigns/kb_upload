package com.oodesigns.ai.transformer;

import com.oodesigns.ai.general.Transformer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StringToStreamTransformer implements Transformer<String, Optional<ByteArrayOutputStream>> {

    private static final Logger logger = Logger.getLogger(StringToStreamTransformer.class.getName());
    private final Function<Integer, ByteArrayOutputStream> getStream;

    public StringToStreamTransformer(final Function<Integer, ByteArrayOutputStream> getStream) {
        this.getStream = getStream;
    }

    @Override
    public Optional<ByteArrayOutputStream> transform(final String input) {
        try {
            return Optional.of(transformToStream(input.toLowerCase().getBytes()));
        } catch (final IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
            return Optional.empty();
        }
    }

    private ByteArrayOutputStream transformToStream(final byte[] bytes) throws IOException {
        final ByteArrayOutputStream outputStream = getStream.apply(bytes.length);
        outputStream.write(bytes);
        return outputStream;
    }
}
