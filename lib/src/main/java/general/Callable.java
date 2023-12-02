package general;

import java.util.function.UnaryOperator;

public interface Callable<T>{
    T calling(UnaryOperator<T> function);
}
