package json;

import general.Callable;
import general.ThrowableElse;

public interface JSONValidatedState<T> extends Callable<T>, ThrowableElse<T, T, RuntimeException> {}
