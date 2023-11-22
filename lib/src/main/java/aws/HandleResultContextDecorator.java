package aws;

import com.amazonaws.services.lambda.runtime.Context;
import kb_upload.Callable;
import kb_upload.ThrowableElse;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class HandleResultContextDecorator<T, U> {
    private static final String RESULT = "RESULT: %s";

    private final Context context;

    public HandleResultContextDecorator(final Context context) {
        this.context = context;
    }

    public HandleResultContextDecorator<T, U> calling(final Supplier<Callable<T>> callableSupplier){
        callableSupplier.get().calling(logResult());
        return this;
    }
    public U orElseThrow(final Supplier<ThrowableElse<U, T, RuntimeException>> throwableElseSupplier){
        return throwableElseSupplier.get().orElseThrow(newException("%s"));
    }

    public U orElseThrow(final Supplier<ThrowableElse<U, T, RuntimeException>> throwableElseSupplier, final String errorFormat){
        return throwableElseSupplier.get().orElseThrow(newException(errorFormat));
    }

    private UnaryOperator<T> logResult() {
        return v -> { context.getLogger().log(String.format(RESULT, v)); return v; };
    }

    private Function<T, RuntimeException> newException(final String errorFormat) {
        return m -> new AWSS3Exception(context, String.format(errorFormat,m.toString()));
    }
}
