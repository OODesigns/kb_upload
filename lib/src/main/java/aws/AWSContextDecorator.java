package aws;

import com.amazonaws.services.lambda.runtime.Context;
import kb_upload.*;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class AWSContextDecorator{
    private static final String RESULT = "RESULT: %s";

    private final Context context;
    private final Validator<JSONSchema, JSON, ValidationResult> validator;

    private AWSContextDecorator(final Context context, final Validator<JSONSchema, JSON, ValidationResult> validator){
        this.context = context;
        this.validator = validator;
    }

    public static AWSContextDecorator of(final Context context, final Validator<JSONSchema, JSON, ValidationResult> validator){
        return new AWSContextDecorator(context, validator);
    }

    public ValidationResult validate(final JSONSchemaData jsonSchema, final JSON fileData) {
        return validator.validate(jsonSchema, fileData)
                .calling(logResult())
                .orElseThrow(newInvalidDataException());
    }

    private Function<ValidationResult, RuntimeException> newInvalidDataException() {
        return validationResult -> new AWSS3Exception(context, validationResult.getMessage());
    }

    private UnaryOperator<ValidationResult> logResult() {
        return v -> { context.getLogger().log(String.format(RESULT, v)); return v; };
    }
}
