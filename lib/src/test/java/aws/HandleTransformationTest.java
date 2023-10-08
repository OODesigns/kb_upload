package aws;

import kb_upload.Retrievable;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import com.amazonaws.services.lambda.runtime.Context;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@MockitoSettings
class HandleTransformationTest {

    @Test
    void passValidJsonConvertToLinesOfText(@Mock final Object nothing,
                                           @Mock final Context context,
                                           @Mock final Retrievable<S3Object, Optional<String>> fileLoader){

        final HandleTransformation handleTransformation
                = new HandleTransformation(fileLoader);

        handleTransformation.handleRequest(nothing, context);

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
//        verify(lambdaLogger, times(1)).log(logData.capture());

//        assertThat(logData.getValue()).contains("ValidatedStateOK");
    }

}