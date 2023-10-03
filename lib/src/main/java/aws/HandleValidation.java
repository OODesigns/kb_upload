package aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import kb_upload.*;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class HandleValidation implements RequestHandler<S3Event, Void> {

    private static final String RESULT = "RESULT: %s";
    public static final String EXPECTED_FILE_NAME = "knowledge.json";
        public static final FileLoader FILE_LOADER = new FileLoader("knowledgeSchema.json");
    public static final JSONSchemaData JSON_SCHEMA = new JSONSchemaData(FILE_LOADER.toString());
    public static final String EXPECTED_FILE_NOT_UPLOADED = "Expected File: knowledge.json was not uploaded";
    private final Retrievable<S3Event, Optional<String>> fileData;
    private final Validator<JSONSchema, JSON, Validation> validator;

    /**
     * Used for testing purposes only
     */
    HandleValidation(final  Retrievable<S3Event, Optional<String>> fileData,
                            final Validator<JSONSchema, JSON, Validation> validator) {
        this.fileData = fileData;
        this.validator = validator;
    }

    public HandleValidation() {
        this.fileData = new S3EventSingleFileData(EXPECTED_FILE_NAME, ()-> AmazonS3ClientBuilder.standard().build());
        this.validator = new JSONValidator(()->JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4));
    }

     @Override
    public Void handleRequest(final S3Event event, final Context context) {
        fileData.retrieve(event)
                .ifPresentOrElse(f->validateFileData(f, context), this::throwFileMissing);
        return null;
    }

    private void validateFileData(final String fileData, final Context context) {
        Optional.of(fileData)
                .flatMap(this::validate)
                .map(logResult(context))
                .filter(notValidFile())
                .ifPresent(this::throwInvalidDataException);
    }

    private void throwFileMissing() {
        throw new ValidationException(EXPECTED_FILE_NOT_UPLOADED);
    }

    private void throwInvalidDataException(final Validation validation) {
        throw new ValidationException(validation.messages().toString());
    }

    private static Predicate<Validation> notValidFile() {
        return Validation -> Validation.state() instanceof ValidatedStateError;
    }

    private Function<Validation, Validation> logResult(final Context context) {
        return v -> { context.getLogger().log(String.format(RESULT, v)); return v; };
    }

    private Optional<Validation> validate(final String dataToValidate) {
        return validator.validate(JSON_SCHEMA, new JSONData(dataToValidate));
    }

}
