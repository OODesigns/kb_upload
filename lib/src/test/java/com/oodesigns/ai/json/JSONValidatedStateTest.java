package com.oodesigns.ai.json;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JSONValidatedStateTest {

    @Test
    void validationClassesReturnClassName(){

        assertThat(new JSONValidatedResultStateOK().toString()).contains("State OK");
        assertThat(new JSONValidatedResultStateError("").toString()).contains("State Error");

    }

}