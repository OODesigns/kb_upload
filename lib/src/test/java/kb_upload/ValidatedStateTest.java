package kb_upload;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ValidatedStateTest {

    @Test
    void validationClassesReturnClassName(){

        assertThat(new ValidatedStateOK().toString()).contains("State OK");
        assertThat(new ValidatedStateError("").toString()).contains("State Error");

    }

}