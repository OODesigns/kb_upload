package kb_upload;

import java.util.function.Function;

public abstract class ModelMakerStateResult implements  ModelMakerState<ModelMakerStateResult>{

    private final String message;

    public ModelMakerStateResult(final String message) {
        this.message = message;
    }

    public ModelMakerStateResult calling(final Function<ModelMakerStateResult, ModelMakerStateResult> function) {
        return function.apply(this);
    }

    @Override
    public String getMessage() {
        return message;
    }


}
