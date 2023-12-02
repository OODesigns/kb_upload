package aws;
import aws.root.*;
import assistant_configuration_creator.HandleResult;
import cloud.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import general.Retrievable;
import general.Transformer;
import maker.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class HandleModelCreation implements RequestHandler<Map<String, String>, Void> {
    private static final String UNABLE_TO_LOAD_FILE = "Unable to load file from bucket: %s and key: %s";
    public static final String UNABLE_TO_CREATE_A_MODEL = "Unable To create a model: %s";
    private static final String ERROR_UNABLE_TO_SAVE_MODEL_FILE = "Error unable to save model file: %s";
    private static final String OK_RESULT = "RESULT S3FileSaverOKState: Model Created";
    private final Retrievable<CloudObjectReference, Optional<InputStream>> fileLoader;
    private final CloudStorable fileStore;
    private final Transformer<InputStream, ModelMakerState<ModelMakerResult>> modelMaker;

    HandleModelCreation(final Retrievable<CloudObjectReference, Optional<InputStream>> fileLoader,
                        final Transformer<InputStream, ModelMakerState<ModelMakerResult>> modelMaker,
                        final CloudStorable fileStore) {
        this.fileLoader = fileLoader;
        this.fileStore = fileStore;
        this.modelMaker = modelMaker;
    }

    public Void handleRequest(final Map<String, String> input, final Context context) {
        Optional.of(getS3ObjectForModelInput(input))
                .map(getFileData())
                .flatMap(createModel())
                .ifPresent(outputStream -> saveToFile(outputStream, input, context));
        return null;
    }


    private void saveToFile(final ByteArrayOutputStream stream, final Map<String, String> input, final Context context) {
        Optional.of(getS3ObjectForModel(input))
                .map(s->fileStore.store(s, stream))
                .filter(hasErrorState())
                .ifPresentOrElse(throwSaveException(),
                ()->context.getLogger().log(OK_RESULT));
    }

    private static Consumer<CloudSaverState<CloudSaverResult>> throwSaveException() {
        return error -> {
            throw new AWSS3Exception(String.format(ERROR_UNABLE_TO_SAVE_MODEL_FILE, error));
        };
    }

    private static Predicate<CloudSaverState<CloudSaverResult>> hasErrorState() {
        return s3FileSaverState -> s3FileSaverState instanceof CloudSaverStateError;
    }

    private Function<InputStream, Optional<ByteArrayOutputStream>> createModel() {
        return inputStream -> {
            try(inputStream){

                final ModelMakerState<ModelMakerResult> transform = modelMaker.transform(inputStream);

                return Optional.of(new HandleResult<ModelMakerResult, ByteArrayOutputStream>()
                        .calling(transform)
                        .orElseThrow(transform, UNABLE_TO_CREATE_A_MODEL));

            } catch (final IOException e) {
                return Optional.empty();
            }
        };

    }

    private Function<CloudObjectReference, InputStream>  getFileData(){
        return s3Object -> fileLoader.retrieve(s3Object).orElseThrow(() -> throwUnableToLoadFile(s3Object));
    }
    private AWSS3Exception throwUnableToLoadFile(final CloudObjectReference cloudObjectReference) {
        return new AWSS3Exception(String.format(UNABLE_TO_LOAD_FILE,
                cloudObjectReference.getStoreName(), cloudObjectReference.getObjectName()));
    }
}
