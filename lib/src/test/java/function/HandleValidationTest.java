package function;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import kb_upload.ValidatedStateError;
import kb_upload.ValidatedStateOK;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@MockitoSettings
public class HandleValidationTest {

    @Test
    void handleRequestWithValidData(@Mock final S3Event s3Event,
                                    @Mock final Context context) {

        final String jsonData = """
                {
                "person":[
                    {
                        "firstName": "John",
                        "lastName": "Doe Doe Doe"
                    },
                    {
                        "firstName": "Jane",
                        "lastName": "Smith"
                    }
                ]}""";


        final TestLogger testLogger = new TestLogger();
        when(context.getLogger()).thenReturn(testLogger);

        final HandleValidation handleValidation = new HandleValidation(s3Event1 -> Optional.of(jsonData));

        handleValidation.handleRequest(s3Event, context);

        assertThat(testLogger.toString()).contains(new ValidatedStateOK().toString());
    }

    @Test
    void handleRequestWithMissingEntries(@Mock final S3Event s3Event,
                                         @Mock final Context context) {

        final String jsonDataMissingEntries = """
                {
                 "$schema": "knowledgeSchema.json",
                  "utterance": [
                    {"name": "confirmation"}
                   ]
                }
                
                """;

        final TestLogger testLogger = new TestLogger();

        when(context.getLogger()).thenReturn(testLogger);

       final HandleValidation handleValidation = new HandleValidation(s3Event1 -> Optional.of(jsonDataMissingEntries));

       assertThrows(RuntimeException.class, () -> handleValidation.handleRequest(s3Event, context));


       assertThat(testLogger.toString())
                .contains(new ValidatedStateError().toString())
                .contains("entries: is missing but it is required");

    }

    @Test
    void handleRequestWithMissingName(@Mock final S3Event s3Event,
                                      @Mock final Context context) {

        final String jsonDataMissingName = """
                {
                  "$schema": "knowledgeSchema.json",
                   "utterance": [
                     { "name": "first name",
                       "entries":"anything"
                     },
                     {"entries":"more anything"}
                   ]
                 }
                
                """;

        final TestLogger testLogger = new TestLogger();

        when(context.getLogger()).thenReturn(testLogger);

        final HandleValidation handleValidation = new HandleValidation(s3Event1 -> Optional.of(jsonDataMissingName));

        assertThrows(RuntimeException.class, () -> handleValidation.handleRequest(s3Event, context));

        assertThat(testLogger.toString())
                .contains(new ValidatedStateError().toString())
                .contains("name: is missing but it is required");

    }

    @Test
    void handleRequestWithMissingNameEntries(@Mock final S3Event s3Event,
                                      @Mock final Context context) {

        final String jsonDataMissingNameEntries = """
                {
                    "$schema": "knowledgeSchema.json",
                     "utterance": [
                       {}
                     ]
                   }
                                
                """;

        final TestLogger testLogger = new TestLogger();

        when(context.getLogger()).thenReturn(testLogger);

        final HandleValidation handleValidation = new HandleValidation(s3Event1 -> Optional.of(jsonDataMissingNameEntries));

        assertThrows(RuntimeException.class, () -> handleValidation.handleRequest(s3Event, context));

        assertThat(testLogger.toString())
                .contains(new ValidatedStateError().toString())
                .contains("name: is missing but it is required")
                .contains("entries: is missing but it is required");

    }

    @Test
    void handleRequestWithInvalidJson(@Mock final S3Event s3Event,
                                      @Mock final Context context) {

        final String jsonDataMissingBracket = """
                {
                    "$schema": "knowledgeSchema.json",
                     "utterance":
                       {}
                     ]
                   }
                                
                """;

        final HandleValidation handleValidation = new HandleValidation(s3Event1 -> Optional.of(jsonDataMissingBracket));

        assertThrows(RuntimeException.class, () -> handleValidation.handleRequest(s3Event, context));
    }
}