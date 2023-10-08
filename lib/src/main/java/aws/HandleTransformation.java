package aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import kb_upload.Retrievable;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.List;
import java.util.Optional;


public class HandleTransformation implements RequestHandler<Object, Void> {

    private static final String UTTERANCE = "utterance";
    private static final String KNOWLEDGE_JSON = "knowledge.json";
    private final Retrievable<S3Object, Optional<String>> fileLoader;

    //Used for testing purposes only
    HandleTransformation(final Retrievable<S3Object, Optional<String>> fileLoader) {
        this.fileLoader = fileLoader;
    }

    public HandleTransformation() {
        this.fileLoader = new S3FileLoader(AmazonS3ClientBuilder::defaultClient);
    }


    @Override
    public Void handleRequest(final Object input, final Context context) {
//        new JSonArrayToList(UTTERANCE)
//                .transform(new S3FileLoader(this::getClient, KNOWLEDGE_JSON).toString())
//                .ifPresent(this::saveToFile);
//
       return null;
    }

    private void saveToFile(final List<String> transformedData) {

    }


    private S3Client getClient() {
        return null;
    }
}
