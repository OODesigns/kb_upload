package aws;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import kb_upload.*;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


public class HandleTransformation implements RequestHandler<Map<String, String>, Void> {

    private static final String UTTERANCE = "utterance";
    private static final String TRANSFORMATION_BUCKET_NAME = "Transformation-BucketName";
    private static final String TRANSFORMATION_KEY_NAME = "Transformation-KeyName";
    private static final String TRANSFORMED_BUCKET_NAME = "Transformed-BucketName";
    private static final String TRANSFORMED_KEY_NAME = "Transformed-KeyName";
    private static final String UNABLE_TO_TRANSFORM_DATA = "Unable to transform data";
    private static final String ERROR_UNABLE_TO_SAVE_TRANSFORMED_FILE = "Error unable to save transformed file: %s";
    private static final String OK_RESULT = "RESULT S3FileSaverOKState";
    private static final String TRANSFORMATION = "transformation";
    private static final String TRANSFORMED = "transformed";
    private final Retrievable<S3Object, Optional<String>> fileLoader;
    private final Transformer1_1<JSON, mappable<List<String>, String, String>> jsonTransformer;

    private final Storable<S3Object, String, S3FileSaverState> fileStore;

    private final S3RequestProvider s3RequestProvider;
    private final Transformer2_1<Context, S3Object, JSON> s3JSONFileDataTransformer;


    //Used for testing purposes only
    HandleTransformation(final Retrievable<S3Object, Optional<String>> fileLoader,
                         final Transformer1_1<JSON, mappable<List<String>, String, String>> jsonTransformer,
                         final Storable<S3Object, String, S3FileSaverState> fileStore,
                         final S3RequestProvider s3RequestProvider) {
        this.fileLoader = fileLoader;
        this.jsonTransformer = jsonTransformer;
        this.fileStore = fileStore;
        this.s3RequestProvider = s3RequestProvider;
        this.s3JSONFileDataTransformer = new S3JSONFileDataTransformer(fileLoader);
    }

    public HandleTransformation() {
        this.s3RequestProvider = new S3Request();
        this.fileLoader = new S3FileLoader(()-> S3Client.builder().build() , s3RequestProvider);
        this.fileStore =  new S3FileSaver(()-> S3Client.builder().build(), s3RequestProvider);
        this.jsonTransformer = new JSonArrayToList(UTTERANCE);
        this.s3JSONFileDataTransformer = new S3JSONFileDataTransformer(fileLoader);
    }


    @Override
    public Void handleRequest(final Map<String, String> input, final Context context) {
        Optional.of(getS3ObjectForTransformation(input, context))
                .map(s->s3JSONFileDataTransformer.transform(context, s))
                .flatMap(transformData(context))
                .ifPresent(data->saveToFile(data, input, context));

        return null;
    }

    private void saveToFile(final String data, final Map<String, String> input, final Context context) {
        Optional.of(getS3ObjectForTransformed(input, context))
                .map(s-> fileStore.store(s, data))
                .filter(hasErrorState())
                .ifPresentOrElse(throwSaveException(context),
                        ()->context.getLogger().log(OK_RESULT));
    }

    private static Predicate<S3FileSaverState> hasErrorState() {
        return s3FileSaverState -> s3FileSaverState instanceof S3FileSaverErrorState;
    }

    private static Consumer<S3FileSaverState> throwSaveException(final Context context) {
        return error -> {
            throw new s3Exception(context,
                    String.format(ERROR_UNABLE_TO_SAVE_TRANSFORMED_FILE, error));
        };
    }

    private Function<JSON, Optional<String>> transformData(final Context context) {
         return json -> jsonTransformer.transform(json)
                 .map(newLineForEachEntry())
                 .orElseThrow(()->new s3Exception(context, UNABLE_TO_TRANSFORM_DATA))
                 .describeConstable();
    }

    private static Function<List<String>, String> newLineForEachEntry() {
        return l -> String.join("\n", l);
    }

    private S3Object getS3ObjectForTransformation(final Map<String, String> input, final Context context) {
        return new S3ObjectFactory(input, context, TRANSFORMATION_BUCKET_NAME, TRANSFORMATION_KEY_NAME, TRANSFORMATION);
    }

    private S3Object getS3ObjectForTransformed(final Map<String, String> input, final Context context) {
        return new S3ObjectFactory(input, context, TRANSFORMED_BUCKET_NAME, TRANSFORMED_KEY_NAME, TRANSFORMED);
    }

}
