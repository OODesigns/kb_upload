package kb_upload;

import aws.AWSS3Exception;
import java.util.function.Function;

public class ValidatedStateOK extends ValidationResult{
    public ValidatedStateOK() {
        super("Validation State OK");
    }

    @Override
    public ValidationResult orElseThrow(final Function<ValidationResult, AWSS3Exception> functionException) throws AWSS3Exception {
        return this;
    }
}
