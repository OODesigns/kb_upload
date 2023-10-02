/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package kb_upload;

import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class KnowledgeTest {
    @Test void nameMissing() {

        final JSON json = new JSONData(new FileLoader("knowledge_name_missing.json").toString());
        final JSONSchema jsonSchema = new JSONSchemaData(new FileLoader("knowledgeSchema.json").toString());

        final Validator<JSONSchema, JSON> validator = new JSONValidator(()->
                JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4));

        final Validated validate = validator.validate(jsonSchema, json).orElse(null);

        assert  validate != null;
        assertThat(validate.state()).isInstanceOf(ValidatedStateError.class);
        assertThat(validate.messages().get(0)).isEqualTo("$.utterance[1].name: is missing but it is required");
    }

    @Test void entryMissing() {

        final JSON json = new JSONData(new FileLoader("knowledge_entry_missing.json").toString());
        final JSONSchema jsonSchema = new JSONSchemaData(new FileLoader("knowledgeSchema.json").toString());

        final Validator<JSONSchema, JSON> validator = new JSONValidator(()->
                JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4));

        final Validated validate = validator.validate(jsonSchema, json).orElse(null);

        assert  validate != null;
        assertThat(validate.state()).isInstanceOf(ValidatedStateError.class);
        assertThat(validate.messages().get(0)).isEqualTo("$.utterance[0].entries: is missing but it is required");
    }

    @Test void nameEntryMissing() {

        final JSON json = new JSONData(new FileLoader("knowledge_name_entry_missing.json").toString());
        final JSONSchema jsonSchema = new JSONSchemaData(new FileLoader("knowledgeSchema.json").toString());

        final Validator<JSONSchema, JSON> validator = new JSONValidator(()->
                JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4));

        final Validated validate = validator.validate(jsonSchema, json).orElse(null);

        assert  validate != null;
        assertThat(validate.state()).isInstanceOf(ValidatedStateError.class);
        assertThat(validate.messages().get(0)).isEqualTo("$.utterance[0].name: is missing but it is required");
        assertThat(validate.messages().get(1)).isEqualTo("$.utterance[0].entries: is missing but it is required");
    }

    @Test void transformJSONToList(){

        final String jsonData = """
                {
                "person"J:[
                    {
                        "firstName": "John",
                        "lastName": "Doe Doe Doe"
                    },
                    {
                        "firstName": "Jane",
                        "lastName": "Smith"
                    }
                ]}""";

        final Transformer<String, Optional<List<String>>> transformer = new JSonArrayToList("person");

        final List<String> transformed = transformer.transform(jsonData).orElse(null);

        assertThat(transformed).isNotNull();
        assertThat(transformed.get(0)).isEqualTo("John Doe Doe Doe");
        assertThat(transformed.get(1)).isEqualTo("Jane Smith");
    }
}
