package com.oodesigns.ai.cloud;
import com.oodesigns.ai.json.JSON;
import com.oodesigns.ai.general.Retrievable;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@MockitoSettings
class CloudJSONFileDataTransformerTest {
    @Test
    void testTransform_successfullyTransforms(
            final @Mock CloudObjectReference cloudObjectReference,
            final @Mock Retrievable<CloudObjectReference, Optional<InputStream>> fileLoaderMock){

        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream("{\"key\": \"value\"}".getBytes());

        when(fileLoaderMock.retrieve(cloudObjectReference)).thenReturn(Optional.of(byteArrayInputStream));

        final CloudJSONFileDataTransformer cloudJSONFileDataTransformer = new CloudJSONFileDataTransformer(new CloudLoad<>(fileLoaderMock));

        final JSON result = cloudJSONFileDataTransformer.transform(cloudObjectReference);

        assertThat(result.get()).contains("{\"key\": \"value\"}");
    }

    @Test
    void testTransform_throwsExceptionWhenFileNotLoaded(
            final @Mock CloudObjectReference cloudObjectReference,
            final @Mock Retrievable<CloudObjectReference, Optional<InputStream>> fileLoaderMock) {

        when(cloudObjectReference.getStoreName()).thenReturn("sample-bucket");
        when(cloudObjectReference.getObjectName()).thenReturn("sample-key");

        when(fileLoaderMock.retrieve(cloudObjectReference)).thenReturn(Optional.empty());

        final CloudJSONFileDataTransformer cloudJSONFileDataTransformer = new CloudJSONFileDataTransformer(new CloudLoad<>(fileLoaderMock));

        final CloudException exception = assertThrows(CloudException.class, () ->
                cloudJSONFileDataTransformer.transform(cloudObjectReference));

        assertEquals("Unable to transform file to JSON", exception.getMessage());
    }

}