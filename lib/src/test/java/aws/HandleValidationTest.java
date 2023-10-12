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

import java.util.List;
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
    void handleRequestWithValidData(@Mock final S3Object s3Object,
                                    @Mock final S3RequestProvider s3RequestProvider,
                                    @Mock final S3Event s3Event,
                                    @Mock final Context context,
                                    @Mock final LambdaLogger lambdaLogger,
                                    @Mock final Validation validation,
                                    @Mock final Validator<JSONSchema, JSON, Validation> validator) {

        when(context.getLogger()).thenReturn(lambdaLogger);
        when(validator.validate(any(),any())).thenReturn(validation);

        when(validation.state()).thenReturn(new ValidatedStateOK());
        when(validation.toString()).thenReturn("ValidatedStateOK");

        final RequestHandler<S3Event, Void> requestHandler
                = new HandleValidation(__->Optional.of(s3Object),
                                      validator,
                                       __-> Optional.of(validJSON),
                                      s3RequestProvider);

        requestHandler.handleRequest(s3Event, context);

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("ValidatedStateOK");

    }

    @Test
    void handleRequestWithINValidData(
                                    @Mock final S3Object s3Object,
                                    @Mock final S3RequestProvider s3RequestProvider,
                                    @Mock final S3Event s3Event,
                                    @Mock final Context context,
                                    @Mock final LambdaLogger lambdaLogger,
                                    @Mock final Validation validation,
                                    @Mock final Validator<JSONSchema, JSON, Validation> validator) {

        when(context.getLogger()).thenReturn(lambdaLogger);
        when(validator.validate(any(),any())).thenReturn(validation);

        final List<String> messages = List.of("message1", "message2");

        when(validation.state()).thenReturn(new ValidatedStateError());
        when(validation.messages()).thenReturn(messages);
        when(validation.toString()).thenReturn("ValidatedStateError");

        final RequestHandler<S3Event, Void> requestHandler
                = new HandleValidation(__->Optional.of(s3Object),
                                       validator,
                                       __-> Optional.of(validJSON),
                                       s3RequestProvider);

        assertThrows(s3Exception.class, ()->requestHandler.handleRequest(s3Event, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(2)).log(logData.capture());

        final List<String> errors = logData.getAllValues();

        assertThat(errors.get(0)).contains("ValidatedStateError");
        assertThat(errors.get(1)).contains("message1");
        assertThat(errors.get(1)).contains("message2");
    }


    @Test
    void handleRequestWithValidDataUnableToLoad(
                                    @Mock final S3Object s3Object,
                                    @Mock final S3RequestProvider s3RequestProvider,
                                    @Mock final S3Event s3Event,
                                    @Mock final Context context,
                                    @Mock final LambdaLogger lambdaLogger,
                                    @Mock final Validator<JSONSchema, JSON, Validation> validator) {

        when(context.getLogger()).thenReturn(lambdaLogger);
        when(s3Object.getBucketName()).thenReturn("bucket");
        when(s3Object.getKeyName()).thenReturn("key");

        final RequestHandler<S3Event, Void> requestHandler
                = new HandleValidation(__->Optional.of(s3Object),
                                      validator,
                                      __->Optional.empty(),
                                      s3RequestProvider);

        assertThrows(s3Exception.class, ()->requestHandler.handleRequest(s3Event, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("Unable to load file from bucket: bucket and key: key");
    }

    @Test
    void handleRequestWithValidDataInvalidObject(@Mock final Retrievable<S3Object, Optional<String>> fileLoader,
                                                 @Mock final S3RequestProvider s3RequestProvider,
                                                 @Mock final S3Event s3Event,
                                                 @Mock final Context context,
                                                 @Mock final LambdaLogger lambdaLogger,
                                                 @Mock final Validator<JSONSchema, JSON, Validation> validator) {

        when(context.getLogger()).thenReturn(lambdaLogger);

        final RequestHandler<S3Event, Void> requestHandler
                = new HandleValidation(unusedEvent -> Optional.empty(), validator, fileLoader, s3RequestProvider);

        assertThrows(s3Exception.class, ()->requestHandler.handleRequest(s3Event, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("Expected S3Object: knowledge.json was not uploaded");
    }


    @Test
    void handleRequestWithDefaultConNoValidFile(@Mock final S3Event s3Event,
                                                @Mock final Context context,
                                                @Mock final LambdaLogger lambdaLogger){

        when(context.getLogger()).thenReturn(lambdaLogger);

        final RequestHandler<S3Event, Void> requestHandler
                = new HandleValidation();

        assertThrows(s3Exception.class, ()->requestHandler.handleRequest(s3Event, context));
    }
}