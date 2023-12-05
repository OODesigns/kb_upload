package maker;

import general.ResultState;

import java.io.ByteArrayOutputStream;

public abstract class ModelMakerResult implements ResultState<ModelMakerResult, ByteArrayOutputStream> {

    private final String message;

    public ModelMakerResult(final String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
