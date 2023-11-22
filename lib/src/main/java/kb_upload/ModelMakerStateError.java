package kb_upload;
import java.io.ByteArrayOutputStream;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class ModelMakerStateError extends ModelMakerResult {
    public ModelMakerStateError(final String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "ModelMaker State Error: "+getMessage();
    }

    @Override
    public ByteArrayOutputStream orElseThrow(final Function<ModelMakerResult, RuntimeException> functionException) throws RuntimeException {
        throw functionException.apply(this);
    }

    @Override
    public ModelMakerResult calling(final UnaryOperator<ModelMakerResult> function) {
        return this;
    }
}

