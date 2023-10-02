package aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.List;
import java.util.Map;


public class HandleTransformation implements RequestHandler<Map<String, Object>, Void> {

    private static final String UTTERANCE = "utterance";
    private static final String KNOWLEDGE_JSON = "knowledge.json";

    @Override
    public Void handleRequest(final Map<String, Object> input, final Context context) {
//        new JSonArrayToList(UTTERANCE)
//                .transform(new S3FileLoader(this::getClient, KNOWLEDGE_JSON).toString())
//                .ifPresent(this::saveToFile);

        return null;
    }

    private void saveToFile(final List<String> transformedData) {

    }


    private S3Client getClient() {
        return null;
    }
}
