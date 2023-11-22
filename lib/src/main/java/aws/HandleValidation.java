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
    private final Validator<JSONSchema, JSON, ValidationResult> validator;
    private final S3ObjectToJSON s3JSONFileDataTransformer;

    HandleValidation(final Validator<JSONSchema, JSON, ValidationResult> validator,
                     final Retrievable<S3Object, Optional<InputStream>> fileLoader){

        this.validator = validator;
        this.s3JSONFileDataTransformer = new S3JSONFileDataTransformer(fileLoader);
    }

    public HandleValidation() {
        this(new JSONValidator(()->JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4)),
             new S3StreamLoader(()-> S3Client.builder().build() , new S3Request()));
    }

     @Override
    public ValidationResult handleRequest(final Map<String, String> input, final Context context) {
        return new HandleValidationContextDecorator(input, context, validator, s3JSONFileDataTransformer).handleRequest();
    }
}
