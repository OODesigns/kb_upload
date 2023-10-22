package kb_upload;
import java.util.Optional;


public interface ThrowableWithMap <T, V, X extends Throwable>{
    Optional<T> orElseMapThrow(Retrievable<V, X> exceptionSupplier) throws X;
}

