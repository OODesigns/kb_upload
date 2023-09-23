package function;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;

import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import kb_upload.*;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class HandleValidation implements RequestHandler<S3Event, Void> {
    public static final FileLoader FILE_LOADER = new FileLoader("knowledgeSchema.json");
    public static final JSONSchemaData JSON_SCHEMA = new JSONSchemaData(FILE_LOADER.toString());
    public static final JSONValidator JSON_VALIDATOR = new JSONValidator();

    @Override
    public Void handleRequest(final S3Event event, final Context context) {
                 getS3Entity(event)
                 .map(getFileData())
                 .flatMap(this::validate)
                 .map(logResult(context))
                .filter(isNotValidFile())
                .ifPresent(this::throwException);
        return null;
    }

    private Function<S3EventNotification.S3Entity, String> getFileData() {
        return s3Entity -> AmazonS3ClientBuilder
                .standard()
                .build()
                .getObjectAsString(s3Entity.getBucket().getName(), s3Entity.getObject().getKey());
    }

    private Optional<S3EventNotification.S3Entity> getS3Entity(final S3Event event) {
        return Optional.of(event.getRecords().get(0).getS3());
    }

    private void throwException(final Validated validated) {
        throw new RuntimeException(validated.messages().toString());
    }

    private static Predicate<Validated> isNotValidFile() {
        return validated -> !(validated.state() instanceof ValidatedStateOK);
    }

    private Function<Validated, Validated> logResult(final Context context) {
        return validated -> {
            context.getLogger().log("RESULT: " + validated);
            return validated;
        };
    }

    private Optional<Validated> validate(final String dataToValidate) {
        return JSON_VALIDATOR.validate(JSON_SCHEMA, new JSONData(dataToValidate));
    }

}
