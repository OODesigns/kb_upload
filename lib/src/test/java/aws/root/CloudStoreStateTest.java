package aws.root;

import cloud.CloudStoreStateError;
import cloud.CloudStoreStateOK;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
class CloudStoreStateTest {

    @Test
    void TransformationStateReturnClassName(){

        assertThat(new CloudStoreStateOK().toString()).contains("S3FileSaverOKState");
        assertThat(new CloudStoreStateError("ERROR").toString()).contains("ERROR");
    }
}
