package com.oodesigns.ai.json;

import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.oodesigns.ai.file.FileLoader;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.when;

@MockitoSettings
class JSONValidatorTest {

    private static final String MISSING_NAME = """           
                {
                 "$schema": "knowledgeSchema.json",
                  "utterance": [
                    {"name": "test name 1",
                    "entries":"test entry 1"},
                    {"entries":"test entry 2"}
                   ]
                }
                """;

    private static final String MISSING_ENTRY = """           
                {
                 "$schema": "knowledgeSchema.json",
                  "utterance": [
                    {"name": "test name 1",
                    "entries":"test entry 1"},
                    {"name":"test name 2"}
                   ]
                }
                """;

    private static final String MISSING_NAME_ENTRY = """           
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

    private static final String VALID_JSON = """           
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

    private static final String IN_VALID_JSON = """           
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

    @Test
    void nameMissingFromDataFile(@Mock final JSON json,
                                 @Mock final JSONSchema jsonSchema) {

        when(json.get()).thenReturn(MISSING_NAME);
        when(jsonSchema.get()).thenReturn(new FileLoader("knowledgeSchema.json").toString());

        final JSONValidator jsonValidator = new JSONValidator(
                JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4));

        Optional.of(jsonValidator.validate(jsonSchema, json))
                .ifPresentOrElse(v -> {
                    assertThat(v).isInstanceOf(JSONValidatedResultStateError.class);
                    assertThat(v.toString()).contains("required property 'name' not found");
                }, () -> fail("Expected Error Validation but got null"));
    }

    @Test
    void entryMissingFromDataFile(@Mock final JSON json,
                                  @Mock final JSONSchema jsonSchema) {

        when(json.get()).thenReturn(MISSING_ENTRY);
        when(jsonSchema.get()).thenReturn(new FileLoader("knowledgeSchema.json").toString());

        final JSONValidator jsonValidator = new JSONValidator(
                JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4));

        Optional.of(jsonValidator.validate(jsonSchema, json))
                .ifPresentOrElse(v -> {
                    assertThat(v).isInstanceOf(JSONValidatedResultStateError.class);
                    assertThat(v.toString()).contains("required property 'entries' not found");
                }, () -> fail("Expected Error Validation but got null"));
    }

    @Test
    void nameEntryMissingFromDataFile(@Mock final JSON json,
                                      @Mock final JSONSchema jsonSchema) {

        when(json.get()).thenReturn(MISSING_NAME_ENTRY);
        when(jsonSchema.get()).thenReturn(new FileLoader("knowledgeSchema.json").toString());

        final JSONValidator jsonValidator = new JSONValidator(
                JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4));

        Optional.of(jsonValidator.validate(jsonSchema, json))
                .ifPresentOrElse(v -> {
                    assertThat(v).isInstanceOf(JSONValidatedResultStateError.class);
                    final String result = v.toString();
                    assertThat(result).contains("required property 'entries' not found");
                    assertThat(result).contains("required property 'name' not found");
                }, () -> fail("Expected Error Validation but got null"));

    }

    @Test
    void validJSONFile(@Mock final JSON json,
                       @Mock final JSONSchema jsonSchema) {

        when(json.get()).thenReturn(VALID_JSON);
        when(jsonSchema.get()).thenReturn(new FileLoader("knowledgeSchema.json").toString());

        final JSONValidator jsonValidator = new JSONValidator(
                JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4));

        Optional.of(jsonValidator.validate(jsonSchema, json))
                .ifPresentOrElse(v -> {
                    assertThat(v).isInstanceOf(JSONValidatedResultStateOK.class);
                    assertThat(v.toString()).contains("Validation State OK");
                }, () -> fail("Expected Valid Validation but got null"));

    }

    @Test
    void inValidJSONFile(@Mock final JSON json,
                         @Mock final JSONSchema jsonSchema) {

        when(json.get()).thenReturn(IN_VALID_JSON);
        when(jsonSchema.get()).thenReturn(new FileLoader("knowledgeSchema.json").toString());

        final JSONValidator jsonValidator = new JSONValidator(
                JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4));

        assertThat(jsonValidator.validate(jsonSchema, json))
                .isInstanceOf(JSONValidatedResultStateError.class);
    }
}
