package cloud;

import general.Callable;
import general.ThrowableElse;

public interface CloudStreamSaverState <T> extends Callable<T>, ThrowableElse<T, T, RuntimeException> {}
