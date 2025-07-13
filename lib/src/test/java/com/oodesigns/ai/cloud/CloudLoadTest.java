package com.oodesigns.ai.cloud;

import com.oodesigns.ai.general.Retrievable;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import com.oodesigns.ai.support.LogCapture;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings
class CloudLoadTest {
    @Test
    void testRetrieveSuccess(@Mock final CloudObjectReference cloudObjectReference,
                             @Mock final Retrievable<CloudObjectReference, Optional<InputStream>> fileLoader,
                             @Mock final ByteArrayInputStream inputStream) {

        // Mock the behavior of the fileLoader to return an InputStream
        when(fileLoader.retrieve(any())).thenReturn(Optional.of(inputStream));
        final CloudLoad<String> cloudLoad = new CloudLoad<>(fileLoader);

        // Act
        final Optional<String> result = cloudLoad.retrieve(cloudObjectReference, _->"Test Data");

        // Assert
        verify(fileLoader, times(1)).retrieve(cloudObjectReference);
        // Verify the retrieve method was called once

        assertThat(result.orElse(null)).contains("Test Data");
    }

    @Test
    void testRetrieveFailure(@Mock final CloudObjectReference cloudObjectReference,
                             @Mock final Retrievable<CloudObjectReference, Optional<InputStream>> fileLoader) {

        when(fileLoader.retrieve(cloudObjectReference)).thenReturn(Optional.empty());
        final CloudLoad<String> cloudLoad = new CloudLoad<>(fileLoader);


        try(final LogCapture logCapture = new LogCapture(CloudLoad.class)) {
            cloudLoad.retrieve(cloudObjectReference, _ -> "");
            verify(fileLoader, times(1)).retrieve(cloudObjectReference);
            assertThat(logCapture.getLogs().getFirst().getMessage()).contains("Unable to load file from store");
        }
    }

    @Test
    void testRetrieveTransformFailureException(
            @Mock final CloudObjectReference cloudObjectReference,
            @Mock final Retrievable<CloudObjectReference, Optional<InputStream>> fileLoader,
            @Mock final ByteArrayInputStream inputStream) {
        // Arrange
        when(fileLoader.retrieve(cloudObjectReference)).thenReturn(Optional.of(inputStream));
        final CloudLoad<String> cloudLoad = new CloudLoad<>(fileLoader);

        // Act
        try(final LogCapture logCapture = new LogCapture(CloudLoad.class)) {
            cloudLoad.retrieve(cloudObjectReference, _ -> {
                throw new IOException("Test exception");
            });
            verify(fileLoader, times(1)).retrieve(cloudObjectReference);

            assertThat(logCapture.getLogs().getFirst().getMessage()).contains("Test exception");
        }
    }
}