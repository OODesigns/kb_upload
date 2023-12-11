package general;
import java.util.function.Function;

@FunctionalInterface
public interface ThrowableElse <T, U, V extends Throwable>{
    T orElseThrow(Function<U, V> functionException) throws V;
}