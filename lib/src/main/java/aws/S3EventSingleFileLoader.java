package aws;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.amazonaws.services.s3.AmazonS3;
import kb_upload.Retrievable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class S3EventSingleFileLoader implements Retrievable<S3Event, Optional<String>> {
    public static final int FIRST_ITEM = 0;
    private final String expectedFileName;
    private final Supplier<AmazonS3> amazonS3;

    public S3EventSingleFileLoader(final String expectedFileName, final Supplier<AmazonS3> amazonS3) {
        this.expectedFileName = expectedFileName;
        this.amazonS3 = amazonS3;
    }

    @Override
    public Optional<String> retrieve(final S3Event s3Event) {
        return getS3Entity(s3Event)
                .map(this::getData);
    }

   private String getData(final S3EventNotification.S3Entity s3Entity) {
        return amazonS3.get().getObjectAsString(s3Entity.getBucket().getName(),
                                                s3Entity.getObject().getKey());
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
            return s3Entity -> s3Entity.getObject().getKey().equalsIgnoreCase(expectedFileName);
    }
}
