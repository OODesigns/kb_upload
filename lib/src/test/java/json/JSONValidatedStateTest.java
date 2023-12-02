package json;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JSONValidatedStateTest {

    @Test
    void validationClassesReturnClassName(){

        assertThat(new JSONValidatedStateOK().toString()).contains("State OK");
        assertThat(new JSONValidatedStateError("").toString()).contains("State Error");

    }

}