package aws;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
class S3FileSaverStateTest {

    @Test
    void TransformationStateReturnClassName(){

        assertThat(new S3FileSaverOKState().toString()).contains("S3FileSaverOKState");
        assertThat(new S3FileSaverErrorState("ERROR").toString()).contains("ERROR");
    }
}
