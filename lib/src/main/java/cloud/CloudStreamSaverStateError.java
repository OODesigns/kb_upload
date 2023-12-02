package cloud;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class CloudStreamSaverStateError extends CloudStreamSaverResult {
    private static final String STREAM_SAVER_STATE_ERROR = "Stream Saver State Error: %s";
    public CloudStreamSaverStateError(final String message) {
        super(String.format(STREAM_SAVER_STATE_ERROR,message));
    }
    @Override
    public CloudStreamSaverResult orElseThrow(final Function<CloudStreamSaverResult, RuntimeException> functionException) throws RuntimeException {
        throw functionException.apply(this);
    }
    @Override
    public CloudStreamSaverResult calling(final UnaryOperator<CloudStreamSaverResult> function) {
        return this;
    }
}


