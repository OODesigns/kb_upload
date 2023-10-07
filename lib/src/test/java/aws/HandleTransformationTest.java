package aws;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import com.amazonaws.services.lambda.runtime.Context;

import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

@MockitoSettings
class HandleTransformationTest {

    @Test
    void passValidJsonConvertToLinesOfText(@Mock final Map<String, Object> nothing,
                                           @Mock final Context context){

        final HandleTransformation handleTransformation = new HandleTransformation();

        handleTransformation.handleRequest(nothing, context);

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
//        verify(lambdaLogger, times(1)).log(logData.capture());

//        assertThat(logData.getValue()).contains("ValidatedStateOK");
    }

}