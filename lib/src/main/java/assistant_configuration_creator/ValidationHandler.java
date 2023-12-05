package assistant_configuration_creator;
import cloud.CloudObjectReference;
import cloud.CloudObjectToJSON;
import file.FileLoader;
import json.JSON;
import json.JSONSchema;
import json.JSONSchemaData;
import json.JSONValidationResult;
import general.Validator;
import java.util.Optional;

public class ValidationHandler {
    public static final FileLoader FILE_LOADER = new FileLoader("knowledgeSchema.json");
    public static final JSONSchemaData JSON_SCHEMA = new JSONSchemaData(FILE_LOADER.toString());
    private final Validator<JSONSchema, JSON, JSONValidationResult> validator;
    private final CloudObjectToJSON cloudObjectToJSON;

    public ValidationHandler(final Validator<JSONSchema, JSON, JSONValidationResult> validator,
                             final CloudObjectToJSON cloudObjectToJSON){
        this.validator = validator;
        this.cloudObjectToJSON = cloudObjectToJSON;
    }

    public void handleRequest(final CloudObjectReference input) {
        Optional.of(input)
                .map(this::getFileData)
                .map(this::validateData);
    }

    private JSON getFileData(final CloudObjectReference input) {
        return cloudObjectToJSON.transform(input);
    }

    private JSONValidationResult validateData(final JSON fileData) {
        return new HandleResult<>(validator.validate(JSON_SCHEMA, fileData))
                .calling()
                .orElseThrow();
    }
}
