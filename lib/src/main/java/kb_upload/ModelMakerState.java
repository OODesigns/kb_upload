package kb_upload;
import java.io.ByteArrayOutputStream;

public interface ModelMakerState<T> extends Callable<T>,
        ThrowableElse<ByteArrayOutputStream, T, RuntimeException> {

    String getMessage();
}
