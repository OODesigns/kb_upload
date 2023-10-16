package aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import kb_upload.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings
public class HandleValidationTest {

    static private final String validJSON = """
            {
            }
            """;

    @Test
    void handleRequestWithValidData(@Mock final S3RequestProvider s3RequestProvider,
                                    @Mock final Context context,
                                    @Mock final LambdaLogger lambdaLogger,
                                    @Mock final Validation validation,
                                    @Mock final Validator<JSONSchema, JSON, Validation> validator) {


        final Map<String, String> input = Map.of("Validation-BucketName", "bucket",
                                                 "Validation-KeyName", "key");

        when(context.getLogger()).thenReturn(lambdaLogger);
        when(validator.validate(any(),any())).thenReturn(validation);

        when(validation.state()).thenReturn(new ValidatedStateOK());
        when(validation.toString()).thenReturn("ValidatedStateOK");

        final RequestHandler<Map<String, String>, Void> requestHandler
                = new HandleValidation(validator,
                __-> Optional.of(new ByteArrayInputStream(validJSON.getBytes())),
                s3RequestProvider);

        requestHandler.handleRequest(input, context);

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("ValidatedStateOK");

    }

    @Test
    void handleRequestWithINValidData(@Mock final S3RequestProvider s3RequestProvider,
                                      @Mock final Context context,
                                      @Mock final LambdaLogger lambdaLogger,
                                      @Mock final Validation validation,
                                      @Mock final Validator<JSONSchema, JSON, Validation> validator){


        final Map<String, String> input = Map.of("Validation-BucketName", "bucket",
                                                 "Validation-KeyName", "key");

        when(context.getLogger()).thenReturn(lambdaLogger);
        when(validator.validate(any(),any())).thenReturn(validation);

        final List<String> messages = List.of("message1", "message2");

        when(validation.state()).thenReturn(new ValidatedStateError());
        when(validation.messages()).thenReturn(messages);
        //Passing valid JSON, so it does not throw an error,
        when(validation.toString()).thenReturn("ValidatedStateError");
        //The validation however returns an error state
        final RequestHandler<Map<String, String>, Void> requestHandler
                = new HandleValidation(validator,
                __-> Optional.of(new ByteArrayInputStream(validJSON.getBytes())),
                s3RequestProvider);

        assertThrows(s3Exception.class, ()->requestHandler.handleRequest(input, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(2)).log(logData.capture());

        final List<String> errors = logData.getAllValues();

        assertThat(errors.get(0)).contains("ValidatedStateError");
        assertThat(errors.get(1)).contains("message1");
        assertThat(errors.get(1)).contains("message2");
    }


    @Test
    void handleRequestWithValidDataUnableToLoad(
                                    @Mock final S3RequestProvider s3RequestProvider,
                                    @Mock final Context context,
                                    @Mock final LambdaLogger lambdaLogger,
                                    @Mock final Validator<JSONSchema, JSON, Validation> validator) {


        final Map<String, String> input = Map.of("Validation-BucketName", "bucket",
                                                 "Validation-KeyName", "key");
        when(context.getLogger()).thenReturn(lambdaLogger);

        final RequestHandler<Map<String, String>, Void> requestHandler
                = new HandleValidation(validator, __-> Optional.empty(), s3RequestProvider);

        assertThrows(s3Exception.class, ()->requestHandler.handleRequest(input, context));
    }


    @Test
    void handleRequestWithDefaultConNoValidFile(@Mock final Context context,
                                                @Mock final LambdaLogger lambdaLogger){

        final Map<String, String> input = Map.of("Validation-BucketName", "bucket",
                                                 "Validation-KeyName", "key");
        when(context.getLogger()).thenReturn(lambdaLogger);

        final RequestHandler<Map<String, String>, Void> requestHandler
                = new HandleValidation();

        assertThrows(s3Exception.class, ()->requestHandler.handleRequest(input, context));
    }
}