package kb_upload;

public interface ValidatedState<T> extends Callable<T>, ThrowableElse<T, T, RuntimeException> {}
