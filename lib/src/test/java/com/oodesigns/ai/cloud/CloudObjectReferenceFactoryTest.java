package com.oodesigns.ai.cloud;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@MockitoSettings
class CloudObjectReferenceFactoryTest {

    @Test
    void createReferenceForNewMove(@Mock final CloudObjectReference input,
                                   @Mock final CloudObjectReference output) {


         when(input.getObjectName()).thenReturn("object");
         when(output.getStoreName()).thenReturn("store");

        final CloudObjectReference cloudObjectReference = CloudObjectReferenceFactory.moveStore(input, output);

        assertThat(cloudObjectReference.getObjectName()).contains("object");
        assertThat(cloudObjectReference.getStoreName()).contains("store");
    }

}