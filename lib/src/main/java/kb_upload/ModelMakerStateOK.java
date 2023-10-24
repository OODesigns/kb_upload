package kb_upload;

import aws.AWSS3Exception;

import java.io.ByteArrayOutputStream;
import java.util.Optional;
import java.util.function.Function;

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
    public Optional<ByteArrayOutputStream> orElseMapThrow(final Function<ModelMakerResult, AWSS3Exception> functionException) throws AWSS3Exception {
        return Optional.of(outputStream);
    }
}
