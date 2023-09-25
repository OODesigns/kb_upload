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
import java.util.stream.Collectors;

public class JSONValidator implements Validator<JSONSchema, JSON> {

    private final Supplier<JsonSchemaFactory> schemaFactory;

    public JSONValidator(final Supplier<JsonSchemaFactory> factory) {
        this.schemaFactory = factory;
    }

    private Set<ValidationMessage> apply(final Knowledge k) {
        return k.knowledgeSchema.validate(k.knowledge);
    }

    @Override
    public Optional<Validated> validate(final JSONSchema knowledgeSchemaData, final JSON knowledgeData) {
        return transformDataToJsonNodes(knowledgeSchemaData, knowledgeData)
                .map(this::apply)
                .map(getValidated());

    }

    private Function<Set<ValidationMessage>, Validated> getValidated() {
        return validationMessages ->
                validationMessages.isEmpty()
                        ? statusOK()
                        : statusError(validationMessages);
    }

    private Validated statusOK() {
        return new Validated(new ValidatedStateOK(), Collections.emptyList());
    }

    private Validated statusError(final Set<ValidationMessage> validationMessages) {
        return new Validated(new ValidatedStateError(),
                                validationMessages
                                        .stream()
                                        .map(ValidationMessage::getMessage)
                                        .collect(Collectors.toList()));
    }

    private Optional<Knowledge> transformDataToJsonNodes(final JSONSchema jsonSchemaData, final JSON knowledgeData){
        try
        {
            return Optional.of(new Knowledge(getKnowledgeSchemaJsonNode(jsonSchemaData),
                                           getKnowledgeJsonNode(knowledgeData)));

        } catch (final JsonProcessingException | JsonSchemaException ex) {
            return Optional.empty();
        }
    }

    private record Knowledge(JsonSchema knowledgeSchema, JsonNode knowledge){}

    private JsonNode getKnowledgeJsonNode(final JSON knowledgeData) throws JsonProcessingException {
        return new ObjectMapper().readTree(knowledgeData.get());
    }

    private JsonSchema getKnowledgeSchemaJsonNode(final JSONSchema jsonSchemaData) throws JsonSchemaException{
        return schemaFactory.get().getSchema(jsonSchemaData.get());
    }
}
