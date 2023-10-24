package kb_upload;

import aws.AWSS3Exception;

import java.io.ByteArrayOutputStream;
import java.util.Optional;
import java.util.function.Function;

public class ModelMakerStateError extends ModelMakerResult {
    public ModelMakerStateError(final String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "ModelMaker State Error: "+getMessage();
    }

    @Override
    public Optional<ByteArrayOutputStream> orElseMapThrow(final Function<ModelMakerResult, AWSS3Exception> functionException) throws AWSS3Exception {
        throw functionException.apply(this);
    }
}

