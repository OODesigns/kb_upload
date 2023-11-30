package aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import kb_upload.*;
import software.amazon.awssdk.services.s3.S3Client;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class HandleModelCreation implements RequestHandler<Map<String, String>, Void> {

    private static final String MODEL_INPUT_BUCKET_NAME = "ModelInput-BucketName";
    private static final String MODEL_INPUT_KEY_NAME = "ModelInput-KeyName";
    private static final String MODEL_INPUT = "Model Input";
    private static final String MODEL = "Model";

    private static final String RESULT = "RESULT: %s";

    private static final String MODEL_BUCKET_NAME = "Model-BucketName";
    private static final String MODEL_KEY_NAME = "Model-KeyName";
    private static final String UNABLE_TO_LOAD_FILE = "Unable to load file from bucket: %s and key: %s";
    public static final String UNABLE_TO_CREATE_A_MODEL = "Unable To create a model: %s";
    private static final String ERROR_UNABLE_TO_SAVE_MODEL_FILE = "Error unable to save model file: %s";
    private static final String OK_RESULT = "RESULT S3FileSaverOKState: Model Created";
    private final Retrievable<S3ObjectReference, Optional<InputStream>> fileLoader;
    private final Storable<S3ObjectReference, ByteArrayOutputStream, S3FileSaverState> fileStore;
    private final Transformer1_1<InputStream, ModelMakerState<ModelMakerResult>> modelMaker;

    HandleModelCreation(final Retrievable<S3ObjectReference, Optional<InputStream>> fileLoader,
                        final Transformer1_1<InputStream, ModelMakerState<ModelMakerResult>> modelMaker,
                        final Storable<S3ObjectReference, ByteArrayOutputStream, S3FileSaverState> fileStore) {
        this.fileLoader = fileLoader;
        this.fileStore = fileStore;
        this.modelMaker = modelMaker;
    }

    public HandleModelCreation() {
        this.fileLoader = new S3StreamLoader(S3Client.builder().build());
        this.fileStore =  new S3StreamSaver(S3Client.builder().build());
        this.modelMaker = new ModelMaker();
    }

    @Override
    public Void handleRequest(final Map<String, String> input, final Context context) {
        Optional.of(getS3ObjectForModelInput(input, context))
                .map(getFileData(context))
                .flatMap(createModel(context))
                .ifPresent(outputStream -> saveToFile(outputStream, input, context));
        return null;
    }


    private void saveToFile(final ByteArrayOutputStream stream, final Map<String, String> input, final Context context) {
        Optional.of(getS3ObjectForModel(input, context))
                .map(s->fileStore.store(s, stream))
                .filter(hasErrorState())
                .ifPresentOrElse(throwSaveException(context),
                ()->context.getLogger().log(OK_RESULT));
    }

    private static Consumer<S3FileSaverState> throwSaveException(final Context context) {
        return error -> {
            throw new AWSS3Exception(context,
                    String.format(ERROR_UNABLE_TO_SAVE_MODEL_FILE, error));
        };
    }

    private static Predicate<S3FileSaverState> hasErrorState() {
        return s3FileSaverState -> s3FileSaverState instanceof S3FileSaverErrorState;
    }

    private Function<InputStream, Optional<ByteArrayOutputStream>> createModel(final Context context) {
        return inputStream -> {
            try(inputStream){

                final ModelMakerState<ModelMakerResult> transform = modelMaker.transform(inputStream);

                return Optional.of(new HandleResultContextDecorator<ModelMakerResult, ByteArrayOutputStream>(context)
                        .calling(()->transform)
                        .orElseThrow(()->transform, UNABLE_TO_CREATE_A_MODEL));

            } catch (final IOException e) {
                log(context, e.getMessage());
                return Optional.empty();
            }
        };

    }

    private Function<S3ObjectReference, InputStream>  getFileData(final Context context){
        return s3Object -> fileLoader.retrieve(s3Object).orElseThrow(() -> throwUnableToLoadFile(context, s3Object));
    }
    private void log(final Context context, final String messages) {
        context.getLogger().log(String.format(RESULT, messages));
    }
    private AWSS3Exception throwUnableToLoadFile(final Context context, final S3ObjectReference s3ObjectReference) {
        return new AWSS3Exception(context, String.format(UNABLE_TO_LOAD_FILE,
                s3ObjectReference.getBucketName(), s3ObjectReference.getKeyName()));
    }

    private S3ObjectReference getS3ObjectForModelInput(final Map<String, String> input, final Context context) {
        return new S3Reference(input, context, MODEL_INPUT_BUCKET_NAME, MODEL_INPUT_KEY_NAME, MODEL_INPUT);
    }

    private S3ObjectReference getS3ObjectForModel(final Map<String, String> input, final Context context) {
        return new S3Reference(input, context, MODEL_BUCKET_NAME, MODEL_KEY_NAME, MODEL);
    }
}
