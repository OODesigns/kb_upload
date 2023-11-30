/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package kb_upload;

import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.when;

@MockitoSettings
class JSONValidatorTest {

    static private final String missingName = """           
                {
                 "$schema": "knowledgeSchema.json",
                  "utterance": [
                    {"name": "test name 1",
                    "entries":"test entry 1"},
                    {"entries":"test entry 2"}
                   ]
                }
                """;

    static private final String missingEntry = """           
                {
                 "$schema": "knowledgeSchema.json",
                  "utterance": [
                    {"name": "test name 1",
                    "entries":"test entry 1"},
                    {"name":"test name 2"}
                   ]
                }
                """;

    static private final String missingNameEntry = """           
                {
                 "$schema": "knowledgeSchema.json",
                  "utterance": [
                    {"name": "test name 1",
                    "entries":"test entry 1"},
                    {"name":"test name 2"},
                    {}
                   ]
                }
                """;

    static private final String validJSON = """           
                {
                 "$schema": "knowledgeSchema.json",
                  "utterance": [
                    {"name": "test name 1",
                    "entries":"test entry 1"},
                    {"name":"test name 2",
                    "entries":"test entry 2"
                    }
                   ]
                }
                """;

    static private final String inValidJSON = """           
                {
                 "$schema": "knowledgeSchema.json",
                  "utterance":
                    {"name": "test name 1",
                    "entries":"test entry 1"},
                    {"name":"test name 2",
                    "entries":"test entry 2"
                    }
                   ]
                }
                """;



    @Test void nameMissingFromDataFile(@Mock final JSON json,
                                       @Mock final JSONSchema jsonSchema) {

        when(json.get()).thenReturn(missingName);
        when(jsonSchema.get()).thenReturn(new FileLoader("knowledgeSchema.json").toString());

        final JSONValidator jsonValidator = new JSONValidator(
                JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4));

        Optional.of(jsonValidator.validate(jsonSchema, json))
                .ifPresentOrElse(v -> {
                    assertThat(v).isInstanceOf(ValidatedStateError.class);
                    assertThat(v.toString()).contains("name: is missing but it is required");}
                   ,()->fail("Expected Error Validation but got null"));
    }

    @Test void entryMissingFromDataFile(@Mock final JSON json,
                                        @Mock final JSONSchema jsonSchema) {

        when(json.get()).thenReturn(missingEntry);
        when(jsonSchema.get()).thenReturn(new FileLoader("knowledgeSchema.json").toString());

        final JSONValidator jsonValidator = new JSONValidator(
                JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4));

        Optional.of(jsonValidator.validate(jsonSchema, json))
                .ifPresentOrElse(v -> {
                            assertThat(v).isInstanceOf(ValidatedStateError.class);
                            assertThat(v.toString()).contains("entries: is missing but it is required");}
                        ,()->fail("Expected Error Validation but got null"));
    }


    @Test void nameEntryMissingFromDataFile(@Mock final JSON json,
                                            @Mock final JSONSchema jsonSchema) {

        when(json.get()).thenReturn(missingNameEntry);
        when(jsonSchema.get()).thenReturn(new FileLoader("knowledgeSchema.json").toString());

        final JSONValidator jsonValidator = new JSONValidator(
                JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4));

        Optional.of(jsonValidator.validate(jsonSchema, json))
                .ifPresentOrElse(v -> {
                            assertThat(v).isInstanceOf(ValidatedStateError.class);
                            assertThat(v.toString())
                                    .contains("entries: is missing but it is required")
                                    .contains("name: is missing but it is required");}
                        ,()->fail("Expected Error Validation but got null"));

    }

    @Test void validJSONFile(@Mock final JSON json,
                             @Mock final JSONSchema jsonSchema) {

        when(json.get()).thenReturn(validJSON);
        when(jsonSchema.get()).thenReturn(new FileLoader("knowledgeSchema.json").toString());

        final JSONValidator jsonValidator = new JSONValidator(
                JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4));

        Optional.of(jsonValidator.validate(jsonSchema, json))
                .ifPresentOrElse(v -> {
                            assertThat(v).isInstanceOf(ValidatedStateOK.class);
                            assertThat(v.toString()).contains("Validation State OK");}
                        ,()->fail("Expected Valid Validation but got null"));

    }

    @Test void inValidJSONFile(@Mock final JSON json,
                             @Mock final JSONSchema jsonSchema) {

        when(json.get()).thenReturn(inValidJSON);
        when(jsonSchema.get()).thenReturn(new FileLoader("knowledgeSchema.json").toString());

        final JSONValidator jsonValidator = new JSONValidator(
                JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4));

        assertThat(jsonValidator.validate(jsonSchema, json)).isInstanceOf(ValidatedStateError.class);
    }
}
