package cloud;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CloudStoreStateErrorTest {

    @Test
    void  passingMessage() {

        final CloudStoreStateError message = new CloudStoreStateError("message");

        assertThat(message.toString()).isEqualTo("message");
    }

}