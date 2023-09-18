package function;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;

public class HandleValidation implements RequestHandler<S3Event, String> {
    @Override
    public String handleRequest(final S3Event event, final Context context) {
        final LambdaLogger logger = context.getLogger();
        final S3EventNotification.S3EventNotificationRecord record = event.getRecords().get(0);
        final String srcBucket = record.getS3().getBucket().getName();
        // Object key may have spaces or unicode non-ASCII characters.
        final String srcKey = record.getS3().getObject().getUrlDecodedKey();

        logger.log("RECORD: " + record);
        logger.log("SOURCE BUCKET: " + srcBucket);
        logger.log("SOURCE KEY: " + srcKey);
        // log execution details
        return srcBucket + "/" + srcKey;
    }
}
