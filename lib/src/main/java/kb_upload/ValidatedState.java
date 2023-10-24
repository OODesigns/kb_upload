package kb_upload;

import aws.AWSS3Exception;

public interface ValidatedState<T> extends Callable<T>, ThrowableElse<T, T, AWSS3Exception> {}
