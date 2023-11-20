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
                                    @Mock final Validator<JSONSchema, JSON, ValidationResult> validator) {


        final Map<String, String> input = Map.of("Validation-BucketName", "bucket",
                                                 "Validation-KeyName", "key");

        when(context.getLogger()).thenReturn(lambdaLogger);
        when(validator.validate(any(),any())).thenReturn(new ValidatedStateOK());

        final RequestHandler<Map<String, String>, ValidationResult> requestHandler
                = new HandleValidation(validator,
                __-> Optional.of(new ByteArrayInputStream(validJSON.getBytes())),
                s3RequestProvider);

        requestHandler.handleRequest(input, context);

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("State OK");

    }

    @Test
    void handleRequestWithINValidData(@Mock final S3RequestProvider s3RequestProvider,
                                      @Mock final Context context,
                                      @Mock final LambdaLogger lambdaLogger,
                                      @Mock final Validator<JSONSchema, JSON, ValidationResult> validator){


        final Map<String, String> input = Map.of("Validation-BucketName", "bucket",
                                                 "Validation-KeyName", "key");

        when(context.getLogger()).thenReturn(lambdaLogger);
        final List<String> messages = List.of("message1", "message2");
        when(validator.validate(any(),any())).thenReturn(new ValidatedStateError(messages));

        final RequestHandler<Map<String, String>, ValidationResult> requestHandler
                = new HandleValidation(validator,
                __-> Optional.of(new ByteArrayInputStream(validJSON.getBytes())),
                s3RequestProvider);

        assertThrows(AWSS3Exception.class, ()->requestHandler.handleRequest(input, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        final List<String> errors = logData.getAllValues();

        assertThat(errors.get(0)).contains("State Error").contains("message1").contains("message2");
    }


    @Test
    void handleRequestWithValidDataUnableToLoad(
                                    @Mock final S3RequestProvider s3RequestProvider,
                                    @Mock final Context context,
                                    @Mock final LambdaLogger lambdaLogger,
                                    @Mock final Validator<JSONSchema, JSON, ValidationResult> validator) {


        final Map<String, String> input = Map.of("Validation-BucketName", "bucket",
                                                 "Validation-KeyName", "key");
        when(context.getLogger()).thenReturn(lambdaLogger);

        final RequestHandler<Map<String, String>, ValidationResult> requestHandler
                = new HandleValidation(validator, __-> Optional.empty(), s3RequestProvider);

        assertThrows(AWSS3Exception.class, ()->requestHandler.handleRequest(input, context));
    }


    @Test
    void handleRequestWithDefaultConNoValidFile(@Mock final Context context,
                                                @Mock final LambdaLogger lambdaLogger){

        final Map<String, String> input = Map.of("Validation-BucketName", "bucket",
                                                 "Validation-KeyName", "key");
        when(context.getLogger()).thenReturn(lambdaLogger);

        final RequestHandler<Map<String, String>, ValidationResult> requestHandler
                = new HandleValidation();

        assertThrows(AWSS3Exception.class, ()->requestHandler.handleRequest(input, context));
    }
}