package com.oodesigns.ai.maker;
import com.oodesigns.ai.file.FileLoader;
import com.oodesigns.ai.general.ResultState;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MockitoSettings
class ModelMakerTest {


    @Test
    public void testTransformWithINValidFileBadCatInput() {
        final FileLoader fileLoader = new FileLoader("badcat.txt");
        final InputStream inputStream = new ByteArrayInputStream(fileLoader.toString().getBytes(StandardCharsets.UTF_8));

        final ModelMaker modelMaker = new ModelMaker();
        final ResultState<ModelMakerResult, ByteArrayOutputStream> transform = modelMaker.transform(inputStream);

        assertThat(transform).isInstanceOf(ModelMakerStateError.class);
    }


    @Test
    public void testTransformWithValidInput() {
        final FileLoader fileLoader = new FileLoader("cat.txt");

        final InputStream inputStream = new ByteArrayInputStream(fileLoader.toString().getBytes(StandardCharsets.UTF_8));

        final ModelMaker modelMaker = new ModelMaker();
        final ResultState<ModelMakerResult, ByteArrayOutputStream> transform = modelMaker.transform(inputStream);

        assertThat(transform).isInstanceOf(ModelMakerStateOK.class);
        assertThat(transform.orElseThrow(null)).isInstanceOf(ByteArrayOutputStream.class);
    }

    @Test
    public void testTransformWithInvalidInput() {
        // Create a mock input that simulates an invalid input stream.
        final String data = "Invalid data format";
        final InputStream inputStream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));

        final ModelMaker modelMaker = new ModelMaker();
        final ResultState<ModelMakerResult, ByteArrayOutputStream> transform = modelMaker.transform(inputStream);

        // In this example, we assume it would return an empty Optional.
        assertThat(transform).isInstanceOf(ModelMakerStateError.class);

    }

    @Test
    public void testTransformWithEmptyInput(){
        // Create a mock empty input.
        final String data = "";
        final InputStream inputStream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));

        final ModelMaker modelMaker = new ModelMaker();
        final ResultState<ModelMakerResult, ByteArrayOutputStream>transform = modelMaker.transform(inputStream);

        // Again, adjust based on expected behavior with empty input. Assuming it returns an empty Optional.
        assertThat(transform).isInstanceOf(ModelMakerStateError.class);
    }

    @Test
    public void testTransformWithException() throws Exception {
        try(final InputStream mockStream = mock(InputStream.class)) {

            when(mockStream.read(any(), anyInt(), anyInt())).thenThrow(new IOException("Forced exception"));

            final ModelMaker modelMaker = new ModelMaker();
            final ResultState<ModelMakerResult, ByteArrayOutputStream> transform = modelMaker.transform(mockStream);

            assertThat(transform).isInstanceOf(ModelMakerStateError.class);
        }
    }
}