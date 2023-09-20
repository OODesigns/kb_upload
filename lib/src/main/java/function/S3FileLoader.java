package function;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import kb_upload.Loader;

public class S3FileLoader implements Loader<String> {

    final S3EventNotification.S3Entity s3;

    public S3FileLoader(final S3Event event) {
        s3 = event.getRecords().get(0).getS3();
    }

    @Override
    public String load() {
        return AmazonS3ClientBuilder
                .standard()
                .build()
                .getObjectAsString(s3.getBucket().getName(),
                                   s3.getObject().getKey());
    }
}
