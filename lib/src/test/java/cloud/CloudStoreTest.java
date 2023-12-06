package cloud;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@MockitoSettings
class CloudStoreTest {

    @Test
    void storeFailureWithException(@Mock final CloudObjectReference cloudObjectReference,
               @Mock final CloudStorable storable,
               @Mock final ByteArrayOutputStream byteArrayOutputStream) {

        when(storable.store(any(), any())).thenReturn(new CloudStoreStateError("test"));

        final CloudStore store = new CloudStore(storable);

        final CloudException cloudException = assertThrows(CloudException.class, () -> store.store(cloudObjectReference, byteArrayOutputStream));

        assertThat(cloudException.getMessage()).contains("Error unable to save file: test");
    }

    @Test
    void storeSuccess(@Mock final CloudObjectReference cloudObjectReference,
                      @Mock final CloudStorable storable,
                      @Mock final ByteArrayOutputStream byteArrayOutputStream) {

        when(storable.store(any(), any())).thenReturn(new CloudStoreStateOK());

        final CloudStore store = new CloudStore(storable);

        final CloudStoreResult cloudStoreResult
                = assertDoesNotThrow(() -> store.store(cloudObjectReference, byteArrayOutputStream));

        assertThat(cloudStoreResult).isInstanceOf(CloudStoreStateOK.class);
    }
}