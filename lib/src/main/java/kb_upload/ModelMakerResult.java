package kb_upload;

import java.util.function.Function;

public abstract class ModelMakerResult implements  ModelMakerState<ModelMakerResult>{

    private final String message;

    public ModelMakerResult(final String message) {
        this.message = message;
    }

    public ModelMakerResult calling(final Function<ModelMakerResult, ModelMakerResult> function) {
        return function.apply(this);
    }

    @Override
    public String getMessage() {
        return message;
    }


}
