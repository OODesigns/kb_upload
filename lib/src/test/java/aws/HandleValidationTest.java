package aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import kb_upload.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
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
    void handleRequestWithValidData(@Mock final S3Event s3Event,
                                    @Mock final Context context,
                                    @Mock final LambdaLogger lambdaLogger,
                                    @Mock final Validation validation,
                                    @Mock final Validator<JSONSchema, JSON, Validation> validator) {

        when(context.getLogger()).thenReturn(lambdaLogger);
        when(validator.validate(any(),any())).thenReturn(Optional.of(validation));

        when(validation.state()).thenReturn(new ValidatedStateOK());
        when(validation.toString()).thenReturn("ValidatedStateOK");

        final RequestHandler<S3Event, Void> requestHandler
                = new HandleValidation(unusedEvent -> Optional.of(validJSON), validator);

        requestHandler.handleRequest(s3Event, context);

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("ValidatedStateOK");

    }

    @Test
    void handleRequestWithDataMissing(@Mock final S3Event s3Event,
                                    @Mock final Context context,
                                    @Mock final LambdaLogger lambdaLogger,
                                    @Mock final Validation validation,
                                    @Mock final Validator<JSONSchema, JSON, Validation> validator) {

        when(context.getLogger()).thenReturn(lambdaLogger);
        when(validator.validate(any(),any())).thenReturn(Optional.of(validation));

        when(validation.state()).thenReturn(new ValidatedStateError());
        when(validation.toString()).thenReturn("ValidatedStateError");

        final RequestHandler<S3Event, Void> requestHandler
                = new HandleValidation(unusedEvent -> Optional.of(validJSON), validator);

        assertThrows(ValidationException.class, ()->requestHandler.handleRequest(s3Event, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("ValidatedStateError");

    }
    @Test
    void handleRequestWithDefaultConNoValidFile(@Mock final S3Event s3Event,
                                                @Mock final Context context){
        final RequestHandler<S3Event, Void> requestHandler
                = new HandleValidation();

        assertThrows(ValidationException.class, ()->requestHandler.handleRequest(s3Event, context));
    }
}