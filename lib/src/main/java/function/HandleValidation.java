package function;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import kb_upload.*;

public class HandleValidation implements RequestHandler<S3Event, String> {
    @Override
    public String handleRequest(final S3Event event, final Context context) {
        final LambdaLogger logger = context.getLogger();

        final JSON knowledgeFile = new JSONData(new S3FileLoader(event).toString());
        final JSONSchema jsonSchema = new JSONSchemaData(new FileLoader("knowledgeSchema.json").toString());

        final Validator<JSONSchema, JSON> validator = new JSONValidator();
        final Validated validate = validator.validate(jsonSchema, knowledgeFile);

        logger.log("RESULT: " + validate);
        return validate.toString();
    }
}
