package kb_upload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class JSONValidator implements Validator<JSONSchema, JSON> {

    private final Supplier<JsonSchemaFactory> factory;

    public JSONValidator(final Supplier<JsonSchemaFactory> factory) {
        this.factory = factory;
    }

    @Override
    public Optional<Validated> validate(final JSONSchema jsonSchemaData, final JSON jsonData) {
//        factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);

        final JsonSchema jsonSchema = factory.getSchema(jsonSchemaData.get());

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode jsonNode;
        try {
            jsonNode = mapper.readTree(jsonData.get());
            final Set<ValidationMessage> validateMessage = jsonSchema.validate(jsonNode);
            if (validateMessage.isEmpty())
                return new Validated(new ValidatedStateOK(), Collections.emptyList());
            else
                return new Validated(new ValidatedStateError(),
                        validateMessage
                                .stream()
                                .map(ValidationMessage::getMessage)
                                .collect(Collectors.toList()));

        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
