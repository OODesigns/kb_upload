package cloud;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class CloudStreamSaverStateOK extends CloudStreamSaverResult {
    public CloudStreamSaverStateOK() {
        super("Stream Saver State OK");
    }

    @Override
    public CloudStreamSaverStateOK orElseThrow(final Function<CloudStreamSaverResult, RuntimeException> functionException) throws RuntimeException {
        return this;
    }

    @Override
    public CloudStreamSaverResult calling(final UnaryOperator<CloudStreamSaverResult> function) {
        return function.apply(this);
    }
}
