package kb_upload;
import java.util.function.Function;

public interface ThrowableElse <T, V, X extends Throwable>{
    T orElseThrow(Function<V, X> functionException) throws X;
}