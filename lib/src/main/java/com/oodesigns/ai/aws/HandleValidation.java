package com.oodesigns.ai.aws;
import com.oodesigns.ai.aws.root.S3CloudObjectReference;
import com.oodesigns.ai.assistant_configuration_creator.ValidationHandler;
import com.oodesigns.ai.cloud.CloudJSONFileDataTransformer;
import com.oodesigns.ai.cloud.CloudObjectReference;
import com.oodesigns.ai.aws.root.S3StreamLoader;
import com.oodesigns.ai.cloud.CloudLoad;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.oodesigns.ai.general.Retrievable;
import com.oodesigns.ai.general.Validator;
import com.oodesigns.ai.json.JSON;
import com.oodesigns.ai.json.JSONSchema;
import com.oodesigns.ai.json.JSONValidationResult;
import com.oodesigns.ai.json.JSONValidator;
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

        this.validationHandler = new ValidationHandler(validator, new CloudJSONFileDataTransformer( new CloudLoad<>(fileLoader)));
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
