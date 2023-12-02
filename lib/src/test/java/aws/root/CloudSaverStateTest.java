package aws.root;

import cloud.CloudSaverStateError;
import cloud.CloudSaverStateOK;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
class CloudSaverStateTest {

    @Test
    void TransformationStateReturnClassName(){

        assertThat(new CloudSaverStateOK().toString()).contains("S3FileSaverOKState");
        assertThat(new CloudSaverStateError("ERROR").toString()).contains("ERROR");
    }
}
