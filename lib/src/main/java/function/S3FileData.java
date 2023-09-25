package function;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.util.Optional;

public class S3FileData implements Retrievable<com.amazonaws.services.lambda.runtime.events.S3Event, java.util.Optional<String>> {
    @Override
    public Optional<String> get(final S3Event s3Event) {
        return getS3Entity(s3Event).map(this::getData);
    }

   private String getData(final S3EventNotification.S3Entity s3Entity) {
        return AmazonS3ClientBuilder
                .standard()
                .build()
                .getObjectAsString(s3Entity.getBucket().getName(), s3Entity.getObject().getKey());
    }

    private Optional<S3EventNotification.S3Entity> getS3Entity(final S3Event event) {
        return Optional.of(event.getRecords().get(0).getS3());
    }
}
