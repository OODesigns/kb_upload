package aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import kb_upload.*;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

public class HandleValidation implements RequestHandler<Map<String, String>, ValidationResult> {
    private static final Validator<JSONSchema, JSON, ValidationResult> defaultValidator
            = new JSONValidator(JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4));
    private static final Retrievable<S3ObjectReference, Optional<InputStream>> defaultFileLoader
            = new S3StreamLoader(S3Client.builder().build());
    private final ValidationHandler validationHandler;

    HandleValidation(final Validator<JSONSchema, JSON, ValidationResult> validator,
                     final Retrievable<S3ObjectReference, Optional<InputStream>> fileLoader){

        this.validationHandler = new ValidationHandler(validator, new S3JSONFileDataTransformer(fileLoader));
    }

    public HandleValidation() {this(defaultValidator, defaultFileLoader);}

     @Override
    public ValidationResult handleRequest(final Map<String, String> input, final Context context) {
        return validationHandler.handleRequest(input, context);
    }
}
