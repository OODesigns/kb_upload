package function;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class S3FileLoader {

    final String data;

    public S3FileLoader(final S3Event event) {
        final S3EventNotification.S3Entity s3 = event.getRecords().get(0).getS3();
        data = AmazonS3ClientBuilder
                .standard()
                .build()
                .getObjectAsString(s3.getBucket().getName(),
                        s3.getObject().getKey());
    }

    @Override
    public String toString() {
        return data;
    }
}
