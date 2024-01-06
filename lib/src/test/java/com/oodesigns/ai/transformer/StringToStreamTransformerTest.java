package com.oodesigns.ai.transformer;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@MockitoSettings
class StringToStreamTransformerTest {

    @Test
    public void testTransformValidInput() {
        final byte[] expectedBytes = "Hello, World".toLowerCase().getBytes();

        final StringToStreamTransformer stringToStreamTransformer =
                new StringToStreamTransformer(ByteArrayOutputStream::new);

        final Optional<ByteArrayOutputStream> transform = stringToStreamTransformer.transform("Hello, World");

        assertThat(transform).isPresent();
        assertThat(transform.get().toByteArray()).contains(expectedBytes);
    }

    @Test
    public void testTransformWithIOException(@Mock final ByteArrayOutputStream byteArrayOutputStream) throws IOException {

        doThrow(new IOException()).when(byteArrayOutputStream).write(any());

        final StringToStreamTransformer stringToStreamTransformer =
                new StringToStreamTransformer(v->byteArrayOutputStream);

        final Optional<ByteArrayOutputStream> transform = stringToStreamTransformer.transform("Hello, World");

        assertThat(transform).isEmpty();
    }

}