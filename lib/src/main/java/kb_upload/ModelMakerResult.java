package kb_upload;

public abstract class ModelMakerResult implements  ModelMakerState<ModelMakerResult>{

    private final String message;

    public ModelMakerResult(final String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
