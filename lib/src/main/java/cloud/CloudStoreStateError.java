package cloud;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class CloudStoreStateError extends CloudStoreResult {
    private static final String STREAM_SAVER_STATE_ERROR = "Stream Saver State Error: %s";
    public CloudStoreStateError(final String message) {
        super(String.format(STREAM_SAVER_STATE_ERROR,message));
    }
    @Override
    public CloudStoreResult orElseThrow(final Function<CloudStoreResult, RuntimeException> functionException) throws RuntimeException {
        throw functionException.apply(this);
    }
    @Override
    public CloudStoreResult calling(final UnaryOperator<CloudStoreResult> function) {
        return this;
    }
}


