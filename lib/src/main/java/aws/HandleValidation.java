package aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import kb_upload.*;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class HandleValidation implements RequestHandler<Map<String, String>, Void> {
    private static final String RESULT = "RESULT: %s";
    private static final String VALIDATION_KEY_NAME = "Validation-KeyName";
    private static final String VALIDATION_BUCKET_NAME = "Validation-BucketName";
    public static final FileLoader FILE_LOADER = new FileLoader("knowledgeSchema.json");
    public static final JSONSchemaData JSON_SCHEMA = new JSONSchemaData(FILE_LOADER.toString());
    private static final String VALIDATION = "validation";
    private final Validator<JSONSchema, JSON, Validation> validator;
    private final Retrievable<S3Object, Optional<InputStream>> fileLoader;
    private final S3RequestProvider s3RequestProvider;
    private final Transformer2_1<Context, S3Object, JSON> s3JSONFileDataTransformer;

    /**
     * Used for testing purposes only
     */
    HandleValidation(final Validator<JSONSchema, JSON, Validation> validator,
                     final Retrievable<S3Object, Optional<InputStream>> fileLoader,
                     final S3RequestProvider s3RequestProvider){
        this.validator = validator;
        this.fileLoader = fileLoader;
        this.s3RequestProvider = s3RequestProvider;
        this.s3JSONFileDataTransformer = new S3JSONFileDataTransformer(fileLoader);
    }

    public HandleValidation() {
        this.s3RequestProvider = new S3Request();
        this.validator = new JSONValidator(()->JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4));
        this.fileLoader = new S3StreamLoader(()-> S3Client.builder().build() , s3RequestProvider);
        this.s3JSONFileDataTransformer = new S3JSONFileDataTransformer(fileLoader);
    }

     @Override
    public Void handleRequest(final Map<String, String> input, final Context context) {
        Optional.of(getS3ObjectForValidation(input , context))
                .map(s-> s3JSONFileDataTransformer.transform(context, s))
                .ifPresent(d->validateData(d, context));
        return null;
    }

    private S3Object getS3ObjectForValidation(final Map<String, String> input, final Context context) {
        return new S3ObjectFactory(input, context, VALIDATION_BUCKET_NAME, VALIDATION_KEY_NAME, VALIDATION);

    }
     private void validateData(final JSON fileData, final Context context) {
        Optional.of(fileData)
                .map(this::validate)
                .map(logResult(context))
                .filter(notValidFile())
                .ifPresent(validation -> throwInvalidDataException(context, validation));
    }

    private void throwInvalidDataException(final Context context, final Validation validation) {
        throw new s3Exception(context, validation.messages().toString());
    }

    private static Predicate<Validation> notValidFile() {
        return Validation -> Validation.state() instanceof ValidatedStateError;
    }

    private Function<Validation, Validation> logResult(final Context context) {
        return v -> { context.getLogger().log(String.format(RESULT, v)); return v; };
    }

    private Validation validate(final JSON data) {
        try {
            return validator.validate(JSON_SCHEMA, data);
        }catch (final JSONException e){
            return new Validated(new ValidatedStateError(), List.of(e.getMessage()));
        }
    }
}
