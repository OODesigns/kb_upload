package cloud;
import general.Retrievable;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        final Optional<String> result = cloudLoad.retrieve(cloudObjectReference, s->"Test Data");

        // Assert
        verify(fileLoader, times(1)).retrieve(cloudObjectReference);
        // Verify the retrieve method was called once

        assertThat(result.orElse(null)).contains("Test Data");
    }

    @Test
    void testRetrieveFailure(@Mock final CloudObjectReference cloudObjectReference,
                             @Mock final Retrievable<CloudObjectReference, Optional<InputStream>> fileLoader) {
        // Arrange
        // Mock the behavior of the fileLoader to return an empty Optional
        when(fileLoader.retrieve(cloudObjectReference)).thenReturn(Optional.empty());
        final CloudLoad<String> cloudLoad = new CloudLoad<>(fileLoader);

        // Act
        final CloudException cloudException = assertThrows(CloudException.class, () -> cloudLoad.retrieve(cloudObjectReference, s -> ""));

        // Assert
        verify(fileLoader, times(1)).retrieve(cloudObjectReference); // Verify the retrieve method was called once

        assertThat(cloudException.getMessage()).contains("Unable to load file from store");
    }
}