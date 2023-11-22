package aws;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import kb_upload.*;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private final Retrievable<S3Object, Optional<InputStream>> fileLoader;

    private final Transformer1_1<JSON, Mappable<List<String>, String, String>> jsonTransformer;

    private final Storable<S3Object, ByteArrayOutputStream, S3FileSaverState> fileStore;

    private final S3RequestProvider s3RequestProvider;
    private final S3ObjectToJSON s3JSONFileDataTransformer;


    //Used for testing purposes only
    HandleTransformation(final Retrievable<S3Object, Optional<InputStream>> fileLoader,
                         final Transformer1_1<JSON, Mappable<List<String>, String, String>> jsonTransformer,
                         final Storable<S3Object, ByteArrayOutputStream, S3FileSaverState> fileStore,
                         final S3RequestProvider s3RequestProvider) {
        this.fileLoader = fileLoader;
        this.jsonTransformer = jsonTransformer;
        this.fileStore = fileStore;
        this.s3RequestProvider = s3RequestProvider;
        this.s3JSONFileDataTransformer = new S3JSONFileDataTransformer(fileLoader);
    }

    public HandleTransformation() {
        this.s3RequestProvider = new S3Request();
        this.fileLoader = new S3StreamLoader(()-> S3Client.builder().build() , s3RequestProvider);
        this.fileStore =  new S3StreamSaver(()-> S3Client.builder().build(), s3RequestProvider);
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

    private void saveToFile(final ByteArrayOutputStream data, final Map<String, String> input, final Context context) {
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
            throw new AWSS3Exception(context,
                    String.format(ERROR_UNABLE_TO_SAVE_TRANSFORMED_FILE, error));
        };
    }

    private Function<JSON, Optional<ByteArrayOutputStream>> transformData(final Context context) {
         return json -> Optional.ofNullable(jsonTransformer.transform(json)
                 .map(newLineForEachEntry())
                 .flatMap(transformToStream())
                 .orElseThrow(() -> new AWSS3Exception(context, UNABLE_TO_TRANSFORM_DATA)));
    }

    private static Function<String, Optional<ByteArrayOutputStream>> transformToStream() {
        return s -> {
            try {
                final byte[] bytes = s.getBytes();
                return Optional.of(new ByteArrayOutputStream(bytes.length) {{
                    write(bytes);
                }});
            } catch (final IOException e) {
                return Optional.empty();
            }
        };
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
