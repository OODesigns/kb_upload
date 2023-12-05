package aws;

import assistant_configuration_creator.ModelCreationHandler;
import aws.root.S3CloudObjectReference;
import aws.root.S3StreamLoader;
import aws.root.S3StreamSaver;
import cloud.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import general.ResultState;
import general.Transformer;
import maker.ModelMaker;
import maker.ModelMakerResult;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;


public class HandleModelCreation implements RequestHandler<Map<String, String>, Void> {
    private static final String MODEL_INPUT_BUCKET_NAME = "ModelInput-BucketName";
    private static final String MODEL_INPUT_KEY_NAME = "ModelInput-KeyName";
    private static final String MODEL_INPUT = "Model Input";
    private static final String MODEL = "Model";
    private static final String MODEL_BUCKET_NAME = "Model-BucketName";
    private static final String MODEL_KEY_NAME = "Model-KeyName";
    private static final Transformer<InputStream, ResultState<ModelMakerResult, ByteArrayOutputStream>> defaultModelMaker = new ModelMaker();
    private static final S3Client s3Client = S3Client.builder().build();
    private static final CloudStorable defaultCloudStorable =  new CloudStore(new S3StreamSaver(s3Client));
    private static final S3StreamLoader fileLoader = new S3StreamLoader(s3Client);
    private static final CloudLoadable<InputStream> defaultCloudLoadable = new CloudLoad<>(fileLoader);

    private static final CloudCopyable defaultCloudCopyer =
            new CloudCopyToNewStore(new CloudLoad<>(fileLoader), defaultCloudStorable);


    private final ModelCreationHandler modelCreationHandler;

    HandleModelCreation(final Transformer<InputStream, ResultState<ModelMakerResult, ByteArrayOutputStream>> modelMaker,
                        final CloudStorable cloudStorable,
                        final CloudLoadable<InputStream> cloudLoadable,
                        final CloudCopyable cloudCopyable) {

        modelCreationHandler = new ModelCreationHandler(modelMaker, cloudStorable, cloudLoadable, cloudCopyable);
    }

    public HandleModelCreation(){
       this(defaultModelMaker, defaultCloudStorable, defaultCloudLoadable, defaultCloudCopyer);
    }

    public Void handleRequest(final Map<String, String> input, final Context context) {
        modelCreationHandler.handleRequest(getS3ObjectForModelCreation(input),
                                           getS3ObjectForModel(input));
        return null;
    }

    private CloudObjectReference getS3ObjectForModelCreation(final Map<String, String> input) {
        return new S3CloudObjectReference(input, MODEL_INPUT_BUCKET_NAME, MODEL_INPUT_KEY_NAME, MODEL_INPUT);
    }

    private CloudObjectReference getS3ObjectForModel(final Map<String, String> input) {
        return new S3CloudObjectReference(input, MODEL_BUCKET_NAME, MODEL_KEY_NAME, MODEL);
    }
}

