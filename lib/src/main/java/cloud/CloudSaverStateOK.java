package cloud;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class CloudSaverStateOK extends CloudSaverResult {
    public CloudSaverStateOK() {
        super("Stream Saver State OK");
    }

    @Override
    public CloudSaverStateOK orElseThrow(final Function<CloudSaverResult, RuntimeException> functionException) throws RuntimeException {
        return this;
    }

    @Override
    public CloudSaverResult calling(final UnaryOperator<CloudSaverResult> function) {
        return function.apply(this);
    }
}
