package com.oodesigns.ai.cloud;
import com.oodesigns.ai.general.Retrievable;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings
class CloudCopyToNewStoreTest {


    @Test
    void testCopySuccessful(@Mock final Retrievable<CloudObjectReference, Optional<InputStream>> fileLoader,
                                   @Mock final CloudStorable toStorable,
                                   @Mock final CloudObjectReference input,
                                   @Mock final CloudObjectReference output) {

        // Mock the behavior of toStorable.store()
        when(toStorable.store(any(), any())).thenReturn(new CloudStoreStateOK());
        when(fileLoader.retrieve(input)).thenReturn(Optional.of(new ByteArrayInputStream("data".getBytes())));

        final CloudLoad<ByteArrayOutputStream> fromLoadable= new CloudLoad<>(fileLoader);
        final CloudCopyToNewStore cloudCopyToNewStore = new CloudCopyToNewStore(fromLoadable, toStorable);

        final CloudStoreResult result = cloudCopyToNewStore.copy(input, output);

        final ArgumentCaptor<ByteArrayOutputStream> captor = ArgumentCaptor.forClass(ByteArrayOutputStream.class);
        verify(toStorable, times(1)).store(any(), captor.capture());

        assertThat(result).isInstanceOf(CloudStoreStateOK.class);
        assertThat(captor.getValue()).hasToString("data");
    }

    @Test
    void testCopyFailure(@Mock final CloudLoad<ByteArrayOutputStream> fromLoadable,
                                @Mock final CloudStorable toStorable,
                                @Mock final CloudObjectReference input,
                                @Mock final CloudObjectReference output) {

        when(fromLoadable.retrieve(any(), any())).thenReturn(Optional.empty());

        final CloudCopyToNewStore  cloudCopyToNewStore = new CloudCopyToNewStore(fromLoadable, toStorable);

        final CloudException cloudException = assertThrows(CloudException.class, () -> cloudCopyToNewStore.copy(input, output));

        assertThat(cloudException.getMessage()).contains("Unable to copy object");
    }
}