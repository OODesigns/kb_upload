package kb_upload;
import java.io.ByteArrayOutputStream;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class ModelMakerStateOK extends ModelMakerResult {

    private final ByteArrayOutputStream outputStream;

    public ModelMakerStateOK(final String message, final ByteArrayOutputStream outputStream) {
        super(message);
        this.outputStream = outputStream;
    }

    @Override
    public String toString() {
        return "ModelMaker State OK: " + getMessage();
    }

    @Override
    public ByteArrayOutputStream orElseThrow(final Function<ModelMakerResult, RuntimeException> functionException) throws RuntimeException {
        return outputStream;
    }

    @Override
    public ModelMakerResult calling(final UnaryOperator<ModelMakerResult> function) {
        return function.apply(this);
    }
}
