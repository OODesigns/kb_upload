package cloud;

import general.Callable;
import general.ThrowableElse;

public interface CloudSaverState<T> extends Callable<T>, ThrowableElse<T, T, RuntimeException> {}
