package function;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import kb_upload.*;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class HandleValidation implements RequestHandler<S3Event, Void> {

    private static final String RESULT = "RESULT: ";
    private final Retrievable<S3Event, Optional<String>> fileData;

    public static final FileLoader FILE_LOADER = new FileLoader("knowledgeSchema.json");
    public static final JSONSchemaData JSON_SCHEMA = new JSONSchemaData(FILE_LOADER.toString());
    public static final JSONValidator JSON_VALIDATOR =
            new JSONValidator(()-> JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4));

    HandleValidation(final Retrievable<S3Event, Optional<String>> fileData) {
        this.fileData = fileData;
    }

    public HandleValidation() {
        this.fileData = new S3FileData();
    }


    @Override
    public Void handleRequest(final S3Event event, final Context context) {
        fileData.get(event)
                .flatMap(this::validate)
                .map(logResult(context))
                .filter(isNotValidFile())
                .ifPresent(this::throwException);
        return null;
    }

    private void throwException(final Validated validated) {
        throw new RuntimeException(validated.messages().toString());
    }

    private static Predicate<Validated> isNotValidFile() {
        return validated -> !(validated.state() instanceof ValidatedStateOK);
    }

    private Function<Validated, Validated> logResult(final Context context) {
        return validated -> {
            context.getLogger().log(RESULT + validated);
            return validated;
        };
    }

    private Optional<Validated> validate(final String dataToValidate) {
        return JSON_VALIDATOR.validate(JSON_SCHEMA, new JSONData(dataToValidate));
    }

}
