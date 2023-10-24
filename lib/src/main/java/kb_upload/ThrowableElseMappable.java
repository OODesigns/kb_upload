package kb_upload;
import java.util.Optional;
import java.util.function.Function;


@FunctionalInterface
public interface ThrowableElseMappable<T, V, X extends Throwable>{
    Optional<T> orElseMapThrow(Function<V, X> functionException) throws X;
}

