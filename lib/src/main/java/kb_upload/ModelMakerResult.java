package kb_upload;

import aws.AWSS3Exception;

import java.io.ByteArrayOutputStream;
import java.util.Optional;
import java.util.function.Function;

public record ModelMakerResult(ModelMakerState modelMakerState,
                               String Message,
                               ByteArrayOutputStream outputStream)
        implements Callable<ModelMakerResult>,
        ThrowableWithMap <ByteArrayOutputStream, ModelMakerResult, AWSS3Exception> {

    @Override
    public ModelMakerResult calling(final Function<ModelMakerResult, ModelMakerResult> function) {
        return function.apply(this);
    }

    @Override
    public Optional<ByteArrayOutputStream> orElseMapThrow(final Retrievable<ModelMakerResult, AWSS3Exception> retrievable) throws AWSS3Exception {
        if (this.modelMakerState() instanceof ModelMakerStateError) {
            throw retrievable.retrieve(this);
        } else {
            return Optional.of(this.outputStream);
        }
    }

}
