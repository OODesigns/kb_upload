package assistant_configuration_creator;
import cloud.CloudException;
import general.Callable;
import general.ThrowableElse;

import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HandleResult<T, U> {
    private static final Logger logger = Logger.getLogger(HandleResult.class.getName());
    private static final String RESULT = "RESULT: %s";

    public HandleResult<T, U> calling(final Callable<T> callable){
        callable.calling(logResult());
        return this;
    }
    public U orElseThrow(final ThrowableElse<U, T, RuntimeException> throwableElse){
        return throwableElse.orElseThrow(newException("%s"));
    }

    public U orElseThrow(final ThrowableElse<U, T, RuntimeException> throwableElse, final String errorFormat){
        return throwableElse.orElseThrow(newException(errorFormat));
    }

    private UnaryOperator<T> logResult() {
        return v -> { logger.log(Level.INFO, String.format(RESULT, v)); return v; };
    }

    private Function<T, RuntimeException> newException(final String errorFormat) {
        return m -> new CloudException(String.format(errorFormat,m.toString()));
    }
}
