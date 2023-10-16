package kb_upload;

import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MockitoSettings
class ModelMakerTest {

    @Test
    public void testTransformWithValidInput() {
        // Create a mock input that simulates a valid input stream for training.
        final String data = "Category1 This is sample training data.\nCategory2 This is another sample.";
        final InputStream inputStream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));

        final ModelMaker modelMaker = new ModelMaker();
        final Optional<ByteArrayOutputStream> result = modelMaker.transform(inputStream);

        assertThat(result).isPresent();
    }

    @Test
    public void testTransformWithInvalidInput() {
        // Create a mock input that simulates an invalid input stream.
        final String data = "Invalid data format";
        final InputStream inputStream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));

        final ModelMaker modelMaker = new ModelMaker();
        final Optional<ByteArrayOutputStream> result = modelMaker.transform(inputStream);

        // In this example, we assume it would return an empty Optional.
        assertThat(result).isEmpty();
    }

    @Test
    public void testTransformWithEmptyInput(){
        // Create a mock empty input.
        final String data = "";
        final InputStream inputStream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));

        final ModelMaker modelMaker = new ModelMaker();
        final Optional<ByteArrayOutputStream> result = modelMaker.transform(inputStream);

        // Again, adjust based on expected behavior with empty input. Assuming it returns an empty Optional.
        assertThat(result).isEmpty();
    }

    @Test
    public void testTransformWithException() throws Exception {
        final InputStream mockStream = mock(InputStream.class);
        when(mockStream.read(any(), anyInt(), anyInt())).thenThrow(new IOException("Forced exception"));

        final ModelMaker modelMaker = new ModelMaker();
        final Optional<ByteArrayOutputStream> result = modelMaker.transform(mockStream);

        assertThat(result).isEmpty();
    }
}