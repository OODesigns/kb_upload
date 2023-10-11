package aws;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import kb_upload.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


public class HandleTransformation implements RequestHandler<Map<String, String>, Void> {

    private static final String UTTERANCE = "utterance";
    public static final String REGION_IS_MISSING_OR_INVALID = "Region name for transformed file is missing or invalid";
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
    public static final String TRANSFORMED_REGION = "Transformed-Region";
    public static final String ERROR_UNABLE_TO_SAVE_TRANSFORMED_FILE = "Error unable to save transformed file: %s";
    public static final String OK_RESULT = "RESULT S3FileSaverOKState";
    private final Retrievable<S3Object, Optional<String>> fileLoader;
    private final Transformer<JSON, mappable<List<String>, String, String>> jsonTransformer;
    private final Retrievable<Region, Storable<S3Object, String, S3FileSaverState>> fileStoreProvider;


    //Used for testing purposes only
    HandleTransformation(final Retrievable<S3Object, Optional<String>> fileLoader,
                         final Transformer<JSON, mappable<List<String>, String, String>> jsonTransformer,
                         final Retrievable<Region, Storable<S3Object, String, S3FileSaverState>> fileStoreProvider) {
        this.fileLoader = fileLoader;
        this.jsonTransformer = jsonTransformer;
        this.fileStoreProvider = fileStoreProvider;
    }

    public HandleTransformation() {
        this.fileLoader = new S3FileLoader(AmazonS3ClientBuilder::defaultClient);
        this.jsonTransformer = new JSonArrayToList(UTTERANCE);
        this.fileStoreProvider = region -> new S3FileSaver(()-> S3Client.builder().region(region).build());
    }


    @Override
    public Void handleRequest(final Map<String, String> input, final Context context) {
        Optional.of(getS3ObjectForTransformation(input, context))
                .flatMap(s->this.getData(s, context))
                .flatMap(transformData(context))
                .ifPresent(data->saveToFile(data, input, context));

        return null;
    }

    private void saveToFile(final String data, final Map<String, String> input, final Context context) {
        Optional.of(getS3ObjectForTransformed(input, context))
                .map(s-> store(data, input, context, s))
                .filter(hasErrorState())
                .ifPresentOrElse(throwSaveException(context),
                        ()->context.getLogger().log(OK_RESULT));
    }

    private static Predicate<S3FileSaverState> hasErrorState() {
        return s3FileSaverState -> s3FileSaverState instanceof S3FileSaverErrorState;
    }

    private S3FileSaverState store(final String data, final Map<String, String> input, final Context context, final S3Object s) {
        return fileStoreProvider
                .retrieve(getRegion(input, context))
                .store(s, data);
    }

    private static Consumer<S3FileSaverState> throwSaveException(final Context context) {
        return error -> {
            throw new TransformationException(context,
                    String.format(ERROR_UNABLE_TO_SAVE_TRANSFORMED_FILE, error));
        };
    }

    private Region getRegion(final Map<String, String> regionName, final Context context) {
        try {
            return Region.of(regionName.get(TRANSFORMED_REGION));
        } catch (final IllegalArgumentException | NullPointerException e) {
            throw new TransformationException(context, REGION_IS_MISSING_OR_INVALID);
        }
    }

    private Function<JSON, Optional<String>> transformData(final Context context) {
         return json -> jsonTransformer.transform(json)
                 .map(Object::toString)
                 .orElseThrow(()->new TransformationException(context, UNABLE_TO_TRANSFORM_DATA))
                 .describeConstable();
    }


    private Optional<JSON> getData(final S3Object s3Object, final Context context) {
        return fileLoader.retrieve(s3Object)
                    .orElseThrow(() -> throwUnableToLoadFile(context, s3Object))
                    .describeConstable()
                    .map(JSONData::new);
    }

    private TransformationException throwUnableToLoadFile(final Context context, final S3Object s3Object) {
        return new TransformationException(context, String.format(UNABLE_TO_LOAD_FILE,
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

    private BucketNameProvider getBucketNameForTransformationFile(final Map<String, String> input, final Context context) {
        try {
            return new BucketName(input.get(TRANSFORMATION_BUCKET_NAME));
        } catch (final InvalidBucketNameException | NullPointerException e) {
            throw new TransformationException(context, BUCKET_NAME_FOR_TRANSFORMATION_IS_MISSING);
        }
    }

    private KeyNameProvider getKeyNameForTransformationFile(final Map<String, String> input, final Context context) {
        try {
            return new KeyName(input.get(TRANSFORMATION_KEY_NAME));
        } catch (final InvalidS3ObjectKeyException | NullPointerException e) {
            throw new TransformationException(context, KEY_NAME_FOR_TRANSFORMATION_IS_MISSING);
        }
    }

    private BucketNameProvider getBucketNameForTransformedFile(final Map<String, String> input, final Context context) {
        try {
            return new BucketName(input.get(TRANSFORMED_BUCKET_NAME));
        } catch (final InvalidBucketNameException | NullPointerException e) {
            throw new TransformationException(context, BUCKET_NAME_FOR_TRANSFORMED_IS_MISSING);
        }
    }

    private KeyNameProvider getKeyNameForTransformedFile(final Map<String, String> input, final Context context) {
        try {
            return new KeyName(input.get(TRANSFORMED_KEY_NAME));
        } catch (final InvalidS3ObjectKeyException | NullPointerException e) {
            throw new TransformationException(context, KEY_NAME_FOR_TRANSFORMED_IS_MISSING);
        }
    }
}
