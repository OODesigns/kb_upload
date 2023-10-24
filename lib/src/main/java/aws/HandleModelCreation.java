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
    private final Retrievable<S3Object, Optional<InputStream>> fileLoader;
    private final Storable<S3Object, ByteArrayOutputStream, S3FileSaverState> fileStore;
    private final Transformer1_1<InputStream, ModelMakerState<ModelMakerResult>> modelMaker;
    private final S3RequestProvider s3RequestProvider;

    HandleModelCreation(final Retrievable<S3Object, Optional<InputStream>> fileLoader,
                        final Transformer1_1<InputStream, ModelMakerState<ModelMakerResult>> modelMaker,
                        final Storable<S3Object, ByteArrayOutputStream, S3FileSaverState> fileStore,
                        final S3RequestProvider s3RequestProvider) {
        this.fileLoader = fileLoader;
        this.s3RequestProvider = s3RequestProvider;
        this.fileStore = fileStore;
        this.modelMaker = modelMaker;
    }

    public HandleModelCreation() {
        this.s3RequestProvider = new S3Request();
        this.fileLoader = new S3StreamLoader(()-> S3Client.builder().build() , s3RequestProvider);
        this.fileStore =  new S3StreamSaver(()-> S3Client.builder().build(), s3RequestProvider);
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

                return modelMaker.transform(inputStream)
                        .calling(logResult(context))
                        .orElseMapThrow(newEnableToCreateModel(context));

            } catch (final IOException e) {
                log(context, e.getMessage());
                return Optional.empty();
            }
        };

    }

    private Function<S3Object, InputStream>  getFileData(final Context context){
        return s3Object -> fileLoader.retrieve(s3Object).orElseThrow(() -> throwUnableToLoadFile(context, s3Object));
    }


    private Function<ModelMakerResult, ModelMakerResult> logResult(final Context context) {
        return v -> { log(context, v.getMessage()); return v; };
    }

    private void log(final Context context, final String messages) {
        context.getLogger().log(String.format(RESULT, messages));
    }

    private Function<ModelMakerResult, AWSS3Exception> newEnableToCreateModel(final Context context) {
         return modelMakerStateResult ->  new AWSS3Exception(context, String.format(UNABLE_TO_CREATE_A_MODEL, modelMakerStateResult.getMessage()));
    }

    private AWSS3Exception throwUnableToLoadFile(final Context context, final S3Object s3Object) {
        return new AWSS3Exception(context, String.format(UNABLE_TO_LOAD_FILE,
                s3Object.getBucketName(), s3Object.getKeyName()));
    }

    private S3Object getS3ObjectForModelInput(final Map<String, String> input, final Context context) {
        return new S3ObjectFactory(input, context, MODEL_INPUT_BUCKET_NAME, MODEL_INPUT_KEY_NAME, MODEL_INPUT);
    }

    private S3Object getS3ObjectForModel(final Map<String, String> input, final Context context) {
        return new S3ObjectFactory(input, context, MODEL_BUCKET_NAME, MODEL_KEY_NAME, MODEL);
    }
}
