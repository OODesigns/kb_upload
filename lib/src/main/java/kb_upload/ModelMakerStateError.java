package kb_upload;

import aws.AWSS3Exception;

import java.io.ByteArrayOutputStream;
import java.util.Optional;

public class ModelMakerStateError extends ModelMakerStateResult {
    public ModelMakerStateError(final String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "ModelMaker State Error: "+getMessage();
    }

    @Override
    public Optional<ByteArrayOutputStream> orElseMapThrow(final Retrievable<ModelMakerStateResult, AWSS3Exception> retrievableException) throws AWSS3Exception {
        throw retrievableException.retrieve(this);
    }

}

