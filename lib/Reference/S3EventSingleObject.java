package aws;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import maker.Retrievable;
import maker.Transformer;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class S3EventSingleObject implements Retrievable<S3Event, Optional<S3Object>> {
    public static final int FIRST_ITEM = 0;
    private final String expectedKeyName;
    private final Transformer<String, BucketNameProvider> bucketNameTransformer;
    private final Transformer<String, KeyNameProvider> keyNameTransformer;


    public S3EventSingleObject(final String expectedKeyName,
                               final Transformer<String, BucketNameProvider> bucketNameTransformer,
                               final Transformer<String, KeyNameProvider> keyNameTransformer
    ) {
        this.expectedKeyName = expectedKeyName;
        this.bucketNameTransformer = bucketNameTransformer;
        this.keyNameTransformer = keyNameTransformer;
    }

    @Override
    public Optional<S3Object> retrieve(final S3Event s3Event) {
        return getS3Entity(s3Event)
                .map(this::getData);
    }

   private S3Object getData(final S3EventNotification.S3Entity s3Entity) {
        return new S3ObjectName(bucketNameTransformer.transform(s3Entity.getBucket().getName()),
                keyNameTransformer.transform(s3Entity.getObject().getKey()));
    }

    private Optional<S3EventNotification.S3Entity> getS3Entity(final S3Event event) {
        return event.getRecords().stream()
                .map(S3EventNotification.S3EventNotificationRecord::getS3)
                .filter(hasValidateName())
                .collect(Collectors.collectingAndThen(Collectors.toList(), getFirstItem()));
    }

    private Function<List<S3EventNotification.S3Entity>, Optional<S3EventNotification.S3Entity>> getFirstItem() {
        return list -> !list.isEmpty() ? Optional.of(list.get(FIRST_ITEM)) : Optional.empty();
    }

    private Predicate<S3EventNotification.S3Entity> hasValidateName() {
        return s3Entity -> s3Entity.getObject().getKey().toLowerCase().contains(expectedKeyName.toLowerCase());
    }
}
