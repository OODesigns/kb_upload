package function;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import kb_upload.*;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class HandleValidation implements RequestHandler<S3Event, Void> {
    @Override
    public Void handleRequest(final S3Event event, final Context context) {
                 getFiles(event)
                .map(this::validate)
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
            context.getLogger().log("RESULT: "+validated);
            return validated;
        };
    }


    private Validated validate(final JsonFile jsonFile) {
        return new JSONValidator().validate(jsonFile.jsonSchema(), jsonFile.knowledgeFile());
    }

    private Optional<JsonFile> getFiles(final S3Event event) {
        return Optional.of(new JsonFile(new JSONData(new S3EventFileLoader(event).toString()),
                new JSONSchemaData(new FileLoader("knowledgeSchema.json").toString())));
    }

    private record JsonFile(JSON knowledgeFile, JSONSchema jsonSchema) {
    }
}
