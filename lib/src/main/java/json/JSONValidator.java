package json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaException;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.ValidationMessage;
import general.Validator;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class JSONValidator implements Validator<JSONSchema, JSON, JSONValidationResult> {

    private final JsonSchemaFactory schemaFactory;

    public JSONValidator(final JsonSchemaFactory factory) {
        this.schemaFactory = factory;
    }

    @Override
    public JSONValidationResult validate(final JSONSchema jsonSchema, final JSON json) {
        return transformDataToJsonNodes(jsonSchema, json)
                .map(this::getSchemaValidationResult);
    }

    private JSONValidationResult getSchemaValidationResult(final JSONNodes jsonNodes) {
        return Optional.of(validate(jsonNodes))
                .filter(hasSchemaErrors())
                .map(this::statusError)
                .orElseGet(JSONValidatedResultStateOK::new);
    }

    private static Predicate<Set<ValidationMessage>> hasSchemaErrors() {
        return validationMessages -> !validationMessages.isEmpty();
    }


    private Set<ValidationMessage> validate(final JSONNodes jsonNodes) {
        return jsonNodes.schema().validate(jsonNodes.node);
    }

    private JSONValidationResult statusError(final Set<ValidationMessage> validationMessages) {
        return new JSONValidatedResultStateError( validationMessages
                                        .stream()
                                        .map(ValidationMessage::getMessage)
                                        .toList());
    }

    private TransformationResult transformDataToJsonNodes(final JSONSchema jsonSchemaData, final JSON knowledgeData){
        try
        {
            return new TransformationResult(new JSONValidatedResultStateOK(), new JSONNodes(getSchemaNode(jsonSchemaData), getNode(knowledgeData)));
        } catch (final JsonProcessingException | JsonSchemaException ex) {
            return new TransformationResult(new JSONValidatedResultStateError(ex.getMessage()), null);
        }
    }

    private record TransformationResult(JSONValidationResult JSONValidationResult, JSONNodes jsonNodes){
        public JSONValidationResult map(final Function<JSONNodes, JSONValidationResult> function) {
            return JSONValidationResult.calling(new FunctionHandler(function, jsonNodes)::handleValidation);
        }

        private record FunctionHandler(Function<JSONNodes, JSONValidationResult> function, JSONNodes jsonNodes) {
            public JSONValidationResult handleValidation(final JSONValidationResult notUsed) {
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
