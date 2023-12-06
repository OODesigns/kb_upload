package aws;

import cloud.CloudException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import general.Validator;
import json.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import support.LogCapture;

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
    public static final String HANDLE_RESULT = "assistant_configuration_creator.HandleResult";

    @Test
    void handleRequestWithValidData(@Mock final Context context,
                                    @Mock final Validator<JSONSchema, JSON, JSONValidationResult> validator) {


        final Map<String, String> input = Map.of("Validation-BucketName", "bucket",
                                                 "Validation-KeyName", "key");

        when(validator.validate(any(),any())).thenReturn(new JSONValidatedResultStateOK());

        final RequestHandler<Map<String, String>, Void> requestHandler
                = new HandleValidation(validator,
                __-> Optional.of(new ByteArrayInputStream(validJSON.getBytes())));

        try(final LogCapture logCapture =new LogCapture(HANDLE_RESULT)){
            requestHandler.handleRequest(input, context);
            assertThat(logCapture.getLogs().get(0).getMessage()).contains("State OK");
        }
    }

    @Test
    void handleRequestWithINValidData(@Mock final Context context,
                                      @Mock final Validator<JSONSchema, JSON, JSONValidationResult> validator){


        final Map<String, String> input = Map.of("Validation-BucketName", "bucket",
                                                 "Validation-KeyName", "key");

        final List<String> messages = List.of("message1", "message2");
        when(validator.validate(any(),any())).thenReturn(new JSONValidatedResultStateError(messages));

        final RequestHandler<Map<String, String>, Void> requestHandler
                = new HandleValidation(validator,
                __-> Optional.of(new ByteArrayInputStream(validJSON.getBytes())));

        final CloudException cloudException = assertThrows(CloudException.class, () -> requestHandler.handleRequest(input, context));
        assertThat(cloudException.getMessage()).contains("State Error").contains("message1").contains("message2");
    }


    @Test
    void handleRequestWithValidDataUnableToLoad(@Mock final Context context,
                                                @Mock final Validator<JSONSchema, JSON, JSONValidationResult> validator) {


        final Map<String, String> input = Map.of("Validation-BucketName", "bucket",
                                                 "Validation-KeyName", "key");

        final RequestHandler<Map<String, String>, Void> requestHandler
                = new HandleValidation(validator, __-> Optional.empty());

        final CloudException cloudException = assertThrows(CloudException.class, () -> requestHandler.handleRequest(input, context));

        assertThat(cloudException.getMessage()).contains("Unable to load");
        verify(validator, never()).validate(any(),any()); //should not be called (no valid file loaded)
    }


    @Test
    void handleRequestWithDefaultConNoValidFile(@Mock final Context context){

        final Map<String, String> input = Map.of("Validation-BucketName", "bucket",
                                                 "Validation-KeyName", "key");

        final RequestHandler<Map<String, String>, Void> requestHandler
                = new HandleValidation();

        final CloudException cloudException = assertThrows(CloudException.class, () -> requestHandler.handleRequest(input, context));
        assertThat(cloudException.getMessage()).contains("Unable to load");
    }
}