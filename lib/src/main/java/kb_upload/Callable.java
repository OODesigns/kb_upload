package kb_upload;

import java.util.function.Function;

public interface Callable<T>{
    T calling(Function<T, T> function);
}
