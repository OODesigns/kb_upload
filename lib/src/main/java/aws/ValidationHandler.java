package aws;

import com.amazonaws.services.lambda.runtime.Context;
import kb_upload.FileLoader;
import kb_upload.JSON;
import kb_upload.JSONSchema;
import kb_upload.JSONSchemaData;
import kb_upload.ValidationResult;
import kb_upload.Validator;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class ValidationHandler {
    private static final String VALIDATION_KEY_NAME = "Validation-KeyName";
    private static final String VALIDATION_BUCKET_NAME = "Validation-BucketName";
    public static final FileLoader FILE_LOADER = new FileLoader("knowledgeSchema.json");
    public static final JSONSchemaData JSON_SCHEMA = new JSONSchemaData(FILE_LOADER.toString());
    private static final String VALIDATION = "validation";
    private final Validator<JSONSchema, JSON, ValidationResult> validator;
    private final S3ObjectToJSON s3JSONFileDataTransformer;

    public ValidationHandler(final Validator<JSONSchema, JSON, ValidationResult> validator,
                             final S3ObjectToJSON s3JSONFileDataTransformer){
        this.validator = validator;
        this.s3JSONFileDataTransformer = s3JSONFileDataTransformer;
    }

    public ValidationResult handleRequest(final Map<String, String> input,
                                          final Context context) {
        return getS3ObjectForValidation(input, context)
                .map(getFileData(context))
                .map(validateData(context))
                .orElseThrow();
    }

    private Function<S3ObjectReference, JSON> getFileData(final Context context) {
        return s -> s3JSONFileDataTransformer.transform(context, s);
    }

    private Optional<S3ObjectReference> getS3ObjectForValidation(final Map<String, String> input,
                                                                 final Context context) {
        return Optional.of(new S3Reference(input, context, VALIDATION_BUCKET_NAME, VALIDATION_KEY_NAME, VALIDATION));
    }

    private Function<JSON, ValidationResult> validateData(final Context context) {
        return fileData ->{
            final ValidationResult validationResult = validator.validate(JSON_SCHEMA, fileData);

            return new HandleResultContextDecorator<ValidationResult, ValidationResult>(context)
                    .calling(()->validationResult)
                    .orElseThrow(()->validationResult);
        };
    }
}
