package aws;
import assistant_configuration_creator.TransformationHandler;
import aws.root.*;
import cloud.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import general.Mappable;
import general.Retrievable;
import general.Transformer;
import json.JSON;
import json.JSONArrayToList;
import software.amazon.awssdk.services.s3.S3Client;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class HandleTransformation implements RequestHandler<Map<String, String>, Void> {

    private static final String UTTERANCE = "utterance";
    private static final String TRANSFORMATION_BUCKET_NAME = "Transformation-BucketName";
    private static final String TRANSFORMATION_KEY_NAME = "Transformation-KeyName";
    private static final String TRANSFORMED_BUCKET_NAME = "Transformed-BucketName";
    private static final String TRANSFORMED_KEY_NAME = "Transformed-KeyName";
    private static final String TRANSFORMATION = "transformation";
    private static final String TRANSFORMED = "transformed";
    private static final S3Client s3Client = S3Client.builder().build();
    private static final CloudStorable defaultCloudStorable = new S3StreamSaver(s3Client);
    private static final Retrievable<CloudObjectReference, Optional<InputStream>> defaultFileLoader = new S3StreamLoader(s3Client);
    private static final CloudLoadable<InputStream> defaultCloudLoadable = new CloudLoad<>( new S3StreamLoader(s3Client));

    private final TransformationHandler transformationHandler;

    HandleTransformation(final Retrievable<CloudObjectReference, Optional<InputStream>> fileLoader,
                         final Transformer<JSON, Mappable<List<String>, String, String>> jsonTransformer,
                         final CloudStorable fileStore) {

        transformationHandler = new TransformationHandler(fileLoader,
                                                          jsonTransformer,
                                                          fileStore);
    }

    public HandleTransformation() {
        this(new S3StreamLoader(s3Client),
             new JSONArrayToList(UTTERANCE),
             defaultCloudStorable);
    }


    @Override
    public Void handleRequest(final Map<String, String> input, final Context context) {

        transformationHandler.handleRequest(getS3ObjectForTransformation(input),
                                            getS3ObjectForTransformed(input));
        return null;
    }

    private CloudObjectReference getS3ObjectForTransformation(final Map<String, String> input) {
        return new S3CloudObjectReference(input, TRANSFORMATION_BUCKET_NAME, TRANSFORMATION_KEY_NAME, TRANSFORMATION);
    }

    private CloudObjectReference getS3ObjectForTransformed(final Map<String, String> input) {
        return new S3CloudObjectReference(input, TRANSFORMED_BUCKET_NAME, TRANSFORMED_KEY_NAME, TRANSFORMED);
    }

}
