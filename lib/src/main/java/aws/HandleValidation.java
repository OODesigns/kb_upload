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

    private static final String RESULT = "RESULT: ";
    public static final String EXPECTED_FILE_NAME = "knowledge.json";
    private final Retrievable<S3Event, Optional<String>> fileData;

    public static final FileLoader FILE_LOADER = new FileLoader("knowledgeSchema.json");
    public static final JSONSchemaData JSON_SCHEMA = new JSONSchemaData(FILE_LOADER.toString());
    public static final Validator<JSONSchema, JSON, Validation> JSON_VALIDATOR =
            new JSONValidator(()-> JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4));

    HandleValidation(final Retrievable<S3Event, Optional<String>> fileData) {
        this.fileData = fileData;
    }

    public HandleValidation() {
        this.fileData = new S3SingleFileData(EXPECTED_FILE_NAME, ()-> AmazonS3ClientBuilder.standard().build());
    }


    @Override
    public Void handleRequest(final S3Event event, final Context context) {
        fileData.retrieve(event)
                .flatMap(this::validate)
                .map(logResult(context))
                .filter(isNotValidFile())
                .ifPresent(this::throwException);

        return null;
    }

    private void throwException(final Validation validation) {
        throw new RuntimeException(validation.messages().toString());
    }

    private static Predicate<Validation> isNotValidFile() {
        return Validation -> !(Validation.state() instanceof ValidatedStateOK);
    }

    private Function<Validation, Validation> logResult(final Context context) {
        return Validation -> {
            context.getLogger().log(RESULT + Validation);
            return Validation;
        };
    }

    private Optional<Validation> validate(final String dataToValidate) {
        return JSON_VALIDATOR.validate(JSON_SCHEMA, new JSONData(dataToValidate));
    }

}
