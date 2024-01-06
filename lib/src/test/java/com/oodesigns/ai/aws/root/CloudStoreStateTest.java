package com.oodesigns.ai.aws.root;

import com.oodesigns.ai.cloud.CloudStoreStateError;
import com.oodesigns.ai.cloud.CloudStoreStateOK;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
class CloudStoreStateTest {

    @Test
    void TransformationStateReturnClassName(){

        assertThat(new CloudStoreStateOK().toString()).contains("Store state OK");
        assertThat(new CloudStoreStateError("ERROR").toString()).contains("ERROR");
    }
}
