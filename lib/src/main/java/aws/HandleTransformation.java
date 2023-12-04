package aws;
import assistant_configuration_creator.TransformationHandler;
import aws.root.*;
import cloud.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import general.Mappable;
import general.Transformer;
import json.JSON;
import json.JSONArrayToList;
import software.amazon.awssdk.services.s3.S3Client;
import java.util.List;
import java.util.Map;


public class HandleTransformation implements RequestHandler<Map<String, String>, Void> {

    private static final String UTTERANCE = "utterance";
    private static final String TRANSFORMATION_BUCKET_NAME = "Transformation-BucketName";
    private static final String TRANSFORMATION_KEY_NAME = "Transformation-KeyName";
    private static final String TRANSFORMED_BUCKET_NAME = "Transformed-BucketName";
    private static final String TRANSFORMED_KEY_NAME = "Transformed-KeyName";
    private static final String TRANSFORMATION = "transformation";
    private static final String TRANSFORMED = "transformed";
    private static final S3Client s3Client = S3Client.builder().build();
    private static final CloudStorable defaultCloudStorable =  new CloudStore(new S3StreamSaver(s3Client));
    private static final S3StreamLoader fileLoader = new S3StreamLoader(s3Client);
    private static final CloudLoadable<String> defaultCloudLoadable = new CloudLoad<>(fileLoader);
    private static final CloudCopyable defaultCloudCopyer =
            new CloudCopyToNewStore(new CloudLoad<>(fileLoader), defaultCloudStorable);


    private final TransformationHandler transformationHandler;

    HandleTransformation(final  CloudLoadable<String> cloudLoadable,
                         final Transformer<JSON, Mappable<List<String>, String, String>> jsonTransformer,
                         final CloudStorable cloudStorable,
                         final CloudCopyable cloudCopyable) {

        transformationHandler = new TransformationHandler(
                new CloudJSONFileDataTransformer(cloudLoadable), jsonTransformer, cloudStorable, cloudCopyable);
    }

    public HandleTransformation() {
        this(defaultCloudLoadable,
             new JSONArrayToList(UTTERANCE),
             defaultCloudStorable,
             defaultCloudCopyer);
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
