package general;

import java.util.function.UnaryOperator;

@FunctionalInterface
public interface Callable<T>{
    T calling(UnaryOperator<T> function);
}
