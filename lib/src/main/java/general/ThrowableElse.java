package general;
import java.util.function.Function;

public interface ThrowableElse <T, U, V extends Throwable>{
    T orElseThrow(Function<U, V> functionException) throws V;
}