package aws;

import com.amazonaws.services.lambda.runtime.Context;
import kb_upload.*;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

public class HandleValidationContextDecorator {
    private static final String VALIDATION_KEY_NAME = "Validation-KeyName";
    private static final String VALIDATION_BUCKET_NAME = "Validation-BucketName";
    public static final FileLoader FILE_LOADER = new FileLoader("knowledgeSchema.json");
    public static final JSONSchemaData JSON_SCHEMA = new JSONSchemaData(FILE_LOADER.toString());
    private static final String VALIDATION = "validation";
    private final Map<String, String> input;
    private final Context context;
    private final Validator<JSONSchema, JSON, ValidationResult> validator;
    private final S3ObjectToJSON s3JSONFileDataTransformer;

    public HandleValidationContextDecorator(final Map<String, String> input,
                                            final Context context,
                                            final Validator<JSONSchema, JSON, ValidationResult> validator,
                                            final S3ObjectToJSON s3JSONFileDataTransformer){
        this.input = input;
        this.context = context;
        this.validator = validator;
        this.s3JSONFileDataTransformer = s3JSONFileDataTransformer;
    }

    public ValidationResult handleRequest() {
        return getS3ObjectForValidation()
                .map(this::getFileData)
                .map(this::validateData)
                .orElseThrow();
    }

    private JSON getFileData(S3Object s) {
        return s3JSONFileDataTransformer.transform(context, s);
    }

    private Optional<S3Object> getS3ObjectForValidation() {
        return Optional.of(new S3ObjectFactory(input, context, VALIDATION_BUCKET_NAME, VALIDATION_KEY_NAME, VALIDATION));

    }
    private ValidationResult validateData(final JSON fileData) {
         final ValidationResult validationResult = validator.validate(JSON_SCHEMA, fileData);

         return new HandleResultContextDecorator<ValidationResult, ValidationResult>(context)
                 .calling(()->validationResult)
                 .orElseThrow(()->validationResult);

    }
}
