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
    private static final String BUCKET_NAME_FOR_TRANSFORMATION_IS_MISSING = "Bucket name for transformation file is missing";
    private static final String KEY_NAME_FOR_TRANSFORMATION_IS_MISSING = "Key name for transformation file is missing";
    private static final String BUCKET_NAME_FOR_TRANSFORMED_IS_MISSING = "Bucket name for transformed file is missing";
    private static final String KEY_NAME_FOR_TRANSFORMED_IS_MISSING = "Key name for transformed file is missing";
    private static final String UNABLE_TO_LOAD_FILE = "Unable to load file from bucket: %s and key: %s";
    public static final String UNABLE_TO_TRANSFORM_DATA = "Unable to transform data";
    public static final String ERROR_UNABLE_TO_SAVE_TRANSFORMED_FILE = "Error unable to save transformed file: %s";
    public static final String OK_RESULT = "RESULT S3FileSaverOKState";
    private final Retrievable<S3Object, Optional<String>> fileLoader;
    private final Transformer<JSON, mappable<List<String>, String, String>> jsonTransformer;

    private final Storable<S3Object, String, S3FileSaverState> fileStore;

    private final S3RequestProvider s3RequestProvider;


    //Used for testing purposes only
    HandleTransformation(final Retrievable<S3Object, Optional<String>> fileLoader,
                         final Transformer<JSON, mappable<List<String>, String, String>> jsonTransformer,
                         final Storable<S3Object, String, S3FileSaverState> fileStore,
                         final S3RequestProvider s3RequestProvider) {
        this.fileLoader = fileLoader;
        this.jsonTransformer = jsonTransformer;
        this.fileStore = fileStore;
        this.s3RequestProvider = s3RequestProvider;
    }

    public HandleTransformation() {
        this.s3RequestProvider = new S3Request();
        this.fileLoader = new S3FileLoader(()-> S3Client.builder().build() , s3RequestProvider);
        this.fileStore =  new S3FileSaver(()-> S3Client.builder().build(), s3RequestProvider);
        this.jsonTransformer = new JSonArrayToList(UTTERANCE);
    }


    @Override
    public Void handleRequest(final Map<String, String> input, final Context context) {
        Optional.of(getS3ObjectForTransformation(input, context))
                .flatMap(s->this.getData(context, s))
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
                 .map(Object::toString)
                 .orElseThrow(()->new s3Exception(context, UNABLE_TO_TRANSFORM_DATA))
                 .describeConstable();
    }


    private Optional<JSON> getData(final Context context, final S3Object s3Object) {
        return fileLoader
                .retrieve(s3Object)
                .orElseThrow(() -> throwUnableToLoadFile(context, s3Object))
                .describeConstable()
                .map(JSONData::new);
    }

    private s3Exception throwUnableToLoadFile(final Context context, final S3Object s3Object) {
        return new s3Exception(context, String.format(UNABLE_TO_LOAD_FILE,
                    s3Object.getBucketName(), s3Object.getKeyName()));
    }

    private S3Object getS3ObjectForTransformation(final Map<String, String> input, final Context context) {
        return new S3ObjectName(getBucketNameForTransformationFile(input, context),
                                getKeyNameForTransformationFile(input, context));
    }

    private S3Object getS3ObjectForTransformed(final Map<String, String> input, final Context context) {
        return new S3ObjectName(getBucketNameForTransformedFile(input, context),
                getKeyNameForTransformedFile(input, context));
    }


    private <T> T getProviderFromInput(final Map<String, String> input,
                                       final String key,
                                       final Context context,
                                       final ProviderConstructor<T> constructor,
                                       final String errorMessage) {
        try {
            return constructor.create(input.get(key));
        } catch (final RuntimeException e) {
            throw new s3Exception(context, errorMessage);
        }
    }

    private interface ProviderConstructor<T> {
        T create(String input) throws RuntimeException;
    }

    private BucketNameProvider getBucketNameForTransformationFile(final Map<String, String> input, final Context context) {
        return getProviderFromInput(
                input,
                TRANSFORMATION_BUCKET_NAME,
                context,
                BucketName::new,
                BUCKET_NAME_FOR_TRANSFORMATION_IS_MISSING
        );
    }

    private KeyNameProvider getKeyNameForTransformationFile(final Map<String, String> input, final Context context) {
        return getProviderFromInput(
                input,
                TRANSFORMATION_KEY_NAME,
                context,
                KeyName::new,
                KEY_NAME_FOR_TRANSFORMATION_IS_MISSING
        );
    }

    private BucketNameProvider getBucketNameForTransformedFile(final Map<String, String> input, final Context context) {
        return getProviderFromInput(
                input,
                TRANSFORMED_BUCKET_NAME,
                context,
                BucketName::new,
                BUCKET_NAME_FOR_TRANSFORMED_IS_MISSING
        );
    }

    private KeyNameProvider getKeyNameForTransformedFile(final Map<String, String> input, final Context context) {
        return getProviderFromInput(
                input,
                TRANSFORMED_KEY_NAME,
                context,
                KeyName::new,
                KEY_NAME_FOR_TRANSFORMED_IS_MISSING
        );
    }




}
