package cloud;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class CloudSaverStateError extends CloudSaverResult {
    private static final String STREAM_SAVER_STATE_ERROR = "Stream Saver State Error: %s";
    public CloudSaverStateError(final String message) {
        super(String.format(STREAM_SAVER_STATE_ERROR,message));
    }
    @Override
    public CloudSaverResult orElseThrow(final Function<CloudSaverResult, RuntimeException> functionException) throws RuntimeException {
        throw functionException.apply(this);
    }
    @Override
    public CloudSaverResult calling(final UnaryOperator<CloudSaverResult> function) {
        return this;
    }
}


