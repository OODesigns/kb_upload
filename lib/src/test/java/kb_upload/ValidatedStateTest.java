package kb_upload;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ValidatedStateTest {

    @Test
    void validationClassesReturnClassName(){

        assertThat(new ValidatedStateOK().toString()).contains("ValidatedStateOK");
        assertThat(new ValidatedStateError().toString()).contains("ValidatedStateError");

    }

}