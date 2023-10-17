package kb_upload;

import java.io.ByteArrayOutputStream;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public record ModelMakerResult(ModelMakerState modelMakerState, String Message, ByteArrayOutputStream outputStream) {
    public ModelMakerResult map(final Function<ModelMakerResult, ModelMakerResult> function) {
        return function.apply(this);
    }

    public Optional<ByteArrayOutputStream> throwOrReturn(final Consumer<ModelMakerResult> m) {
        if (this.modelMakerState() instanceof ModelMakerStateError) {
            m.accept(this);
            return Optional.empty();
        } else {
            return Optional.of(this.outputStream);
        }
    }
}
