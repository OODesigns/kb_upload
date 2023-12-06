package cloud;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CloudStoreStateOKTest {

    @Test
    void  expectedString(){
        final CloudStoreStateOK state = new CloudStoreStateOK();

        assertThat(state.toString()).contains("OK");
    }


}