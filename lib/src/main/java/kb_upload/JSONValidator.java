package kb_upload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaException;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.ValidationMessage;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class JSONValidator implements Validator<JSONSchema, JSON, ValidationResult> {

    private final JsonSchemaFactory schemaFactory;

    public JSONValidator(final JsonSchemaFactory factory) {
        this.schemaFactory = factory;
    }

    @Override
    public ValidationResult validate(final JSONSchema jsonSchema, final JSON json) {
        return transformDataToJsonNodes(jsonSchema, json)
                .map(this::getSchemaValidationResult);
    }

    private ValidationResult getSchemaValidationResult(final JSONNodes jsonNodes) {
        return Optional.of(validate(jsonNodes))
                .filter(hasSchemaErrors())
                .map(this::statusError)
                .orElseGet(ValidatedStateOK::new);
    }

    private static Predicate<Set<ValidationMessage>> hasSchemaErrors() {
        return validationMessages -> !validationMessages.isEmpty();
    }


    private Set<ValidationMessage> validate(final JSONNodes jsonNodes) {
        return jsonNodes.schema().validate(jsonNodes.node);
    }

    private ValidationResult statusError(final Set<ValidationMessage> validationMessages) {
        return new ValidatedStateError( validationMessages
                                        .stream()
                                        .map(ValidationMessage::getMessage)
                                        .toList());
    }

    private TransformationResult transformDataToJsonNodes(final JSONSchema jsonSchemaData, final JSON knowledgeData){
        try
        {
            return new TransformationResult(new ValidatedStateOK(), new JSONNodes(getSchemaNode(jsonSchemaData), getNode(knowledgeData)));
        } catch (final JsonProcessingException | JsonSchemaException ex) {
            return new TransformationResult(new ValidatedStateError(ex.getMessage()), null);
        }
    }

    private record TransformationResult(ValidationResult validationResult, JSONNodes jsonNodes){
        public ValidationResult map(final Function<JSONNodes, ValidationResult> function) {
            return validationResult.calling(new FunctionHandler(function, jsonNodes)::handleValidation);
        }

        private record FunctionHandler(Function<JSONNodes, ValidationResult> function, JSONNodes jsonNodes) {
            public ValidationResult handleValidation(final ValidationResult notUsed) {
                return function.apply(jsonNodes);
            }
        }
    }

    private record JSONNodes(JsonSchema schema, JsonNode node){}

    private JsonNode getNode(final JSON json) throws JsonProcessingException {
        return new ObjectMapper().readTree(json.get());
    }

    private JsonSchema getSchemaNode(final JSONSchema jsonSchemaData) throws JsonSchemaException{
        return schemaFactory.getSchema(jsonSchemaData.get());
    }
}
