package aws;
import aws.root.S3CloudObjectReference;
import assistant_configuration_creator.ValidationHandler;
import cloud.CloudJSONFileDataTransformer;
import cloud.CloudObjectReference;
import aws.root.S3StreamLoader;
import cloud.CloudStreamLoader;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import json.JSON;
import json.JSONSchema;
import json.JSONValidator;
import general.Retrievable;
import json.JSONValidationResult;
import general.Validator;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

public class HandleValidation implements RequestHandler<Map<String, String>, Void> {
    private static final String VALIDATION = "validation";
    private static final String VALIDATION_KEY_NAME = "Validation-KeyName";
    private static final String VALIDATION_BUCKET_NAME = "Validation-BucketName";
    private static final Validator<JSONSchema, JSON, JSONValidationResult> defaultValidator
            = new JSONValidator(JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4));
    private static final Retrievable<CloudObjectReference, Optional<InputStream>> defaultFileLoader
            = new S3StreamLoader(S3Client.builder().build());
    private final ValidationHandler validationHandler;

    HandleValidation(final Validator<JSONSchema, JSON, JSONValidationResult> validator,
                     final Retrievable<CloudObjectReference, Optional<InputStream>> fileLoader){

        this.validationHandler = new ValidationHandler(validator, new CloudJSONFileDataTransformer( new CloudStreamLoader<>(fileLoader)));
    }

    public HandleValidation() {this(defaultValidator, defaultFileLoader);}

    @Override
    public Void handleRequest(final Map<String, String> input, final Context context) {
        validationHandler.handleRequest(new S3CloudObjectReference(input,
                                                                  VALIDATION_BUCKET_NAME,
                                                                  VALIDATION_KEY_NAME,
                                                                  VALIDATION));
        return null;
    }
}
