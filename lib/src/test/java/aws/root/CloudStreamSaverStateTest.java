package aws.root;

import cloud.CloudStreamSaverStateError;
import cloud.CloudStreamSaverStateOK;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
class CloudStreamSaverStateTest {

    @Test
    void TransformationStateReturnClassName(){

        assertThat(new CloudStreamSaverStateOK().toString()).contains("S3FileSaverOKState");
        assertThat(new CloudStreamSaverStateError("ERROR").toString()).contains("ERROR");
    }
}
