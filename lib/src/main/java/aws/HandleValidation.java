package aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import kb_upload.*;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class HandleValidation implements RequestHandler<S3Event, Void> {

    private static final String RESULT = "RESULT: %s";
    public static final String EXPECTED_OBJECT_NAME = "knowledge.json";
    public static final FileLoader FILE_LOADER = new FileLoader("knowledgeSchema.json");
    public static final JSONSchemaData JSON_SCHEMA = new JSONSchemaData(FILE_LOADER.toString());
    private static final String EXPECTED_OBJECT_NOT_UPLOADED = "Expected S3Object: knowledge.json was not uploaded";
    private static final String UNABLE_TO_LOAD_FILE = "Unable to load file from bucket: %s and key: %s";
    private final Retrievable<S3Event, Optional<S3Object>> s3ObjectProvider;
    private final Validator<JSONSchema, JSON, Validation> validator;
    private final Retrievable<S3Object, Optional<String>> fileLoader;

    private final S3RequestProvider s3RequestProvider;

    /**
     * Used for testing purposes only
     */
    HandleValidation(final  Retrievable<S3Event, Optional<S3Object>> s3ObjectProvider,
                            final Validator<JSONSchema, JSON, Validation> validator,
                            final Retrievable<S3Object, Optional<String>> fileLoader,
                            final S3RequestProvider s3RequestProvider){
        this.s3ObjectProvider = s3ObjectProvider;
        this.validator = validator;
        this.fileLoader = fileLoader;
        this.s3RequestProvider = s3RequestProvider;
    }

    public HandleValidation() {
        this.s3RequestProvider = new S3Request();
        this.s3ObjectProvider = new S3EventSingleObject(EXPECTED_OBJECT_NAME, BucketName::new, KeyName::new);
        this.validator = new JSONValidator(()->JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4));
        this.fileLoader = new S3FileLoader(()-> S3Client.builder().build() , s3RequestProvider);
    }

     @Override
    public Void handleRequest(final S3Event event, final Context context) {
        s3ObjectProvider.retrieve(event)
                .ifPresentOrElse(o-> validateData(context, o),
                        ()->this.throwObjectMissing(context));
        return null;
    }

    private void validateData(final Context context, final S3Object s3Object) {
        fileLoader.retrieve(s3Object).
                ifPresentOrElse(f->validateData(f, context),
                        ()->throwUnableToLoadFile(context, s3Object));
    }

     private void validateData(final String fileData, final Context context) {
        Optional.of(fileData)
                .map(this::validate)
                .map(logResult(context))
                .filter(notValidFile())
                .ifPresent(validation -> throwInvalidDataException(context, validation));
    }

    private void throwUnableToLoadFile(final Context context, final S3Object s3Object) {
        throw new s3Exception(context, String.format(UNABLE_TO_LOAD_FILE,
                                               s3Object.getBucketName(),
                                               s3Object.getKeyName()));
    }

    private void throwObjectMissing(final Context context){
        throw new s3Exception(context, EXPECTED_OBJECT_NOT_UPLOADED);
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

    private Validation validate(final String dataToValidate) {
        try {
            return validator.validate(JSON_SCHEMA, new JSONData(dataToValidate));
        }catch (final JSONException e){
            return new Validated(new ValidatedStateError(), List.of(e.getMessage()));
        }
    }

}
