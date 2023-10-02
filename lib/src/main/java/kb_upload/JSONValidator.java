package kb_upload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaException;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.ValidationMessage;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class JSONValidator implements Validator<JSONSchema, JSON, Validation> {

    private final Supplier<JsonSchemaFactory> schemaFactory;

    public JSONValidator(final Supplier<JsonSchemaFactory> factory) {
        this.schemaFactory = factory;
    }

    @Override
    public Optional<Validation> validate(final JSONSchema jsonSchema, final JSON json) {
        return transformDataToJsonNodes(jsonSchema, json)
                .map(this::apply)
                .map(getValidation());

    }

    private Set<ValidationMessage> apply(final JSONNodes jsonNodes) {
        return jsonNodes.schema().validate(jsonNodes.node);
    }

    private Function<Set<ValidationMessage>, Validated> getValidation() {
        return validationMessages ->
                validationMessages.isEmpty() ? statusOK() : statusError(validationMessages);
    }

    private Validated statusOK() {
        return new Validated(new ValidatedStateOK(), Collections.emptyList());
    }

    private Validated statusError(final Set<ValidationMessage> validationMessages) {
        return new Validated(new ValidatedStateError(),
                                validationMessages
                                        .stream()
                                        .map(ValidationMessage::getMessage)
                                        .toList());
    }

    private Optional<JSONNodes> transformDataToJsonNodes(final JSONSchema jsonSchemaData, final JSON knowledgeData){
        try
        {
            return Optional.of(new JSONNodes(getSchemaNode(jsonSchemaData), getNode(knowledgeData)));
        } catch (final JsonProcessingException | JsonSchemaException ex) {
            return Optional.empty();
        }
    }

    private record JSONNodes(JsonSchema schema, JsonNode node){}

    private JsonNode getNode(final JSON json) throws JsonProcessingException {
        return new ObjectMapper().readTree(json.get());
    }

    private JsonSchema getSchemaNode(final JSONSchema jsonSchemaData) throws JsonSchemaException{
        return schemaFactory.get().getSchema(jsonSchemaData.get());
    }
}
