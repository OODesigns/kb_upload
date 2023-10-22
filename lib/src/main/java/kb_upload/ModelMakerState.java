package kb_upload;

import aws.AWSS3Exception;

import java.io.ByteArrayOutputStream;

public interface ModelMakerState<T> extends Callable<T>,
        ThrowableWithMap <ByteArrayOutputStream, T, AWSS3Exception> {

    String getMessage();
}
