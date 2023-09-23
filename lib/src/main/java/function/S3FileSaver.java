package function;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Client;


public class S3FileSaver{

//    private final S3Client s3Client;
//    private final String fileName;
//
//    public S3FileSaver(final S3Client s3Client, final String fileName) {
//        this.s3Client = s3Client;
//        this.fileName = fileName;
//    }
//
//    public S3FileSaver(final S3Event event) {
//        final S3EventNotification.S3Entity s3 = event.getRecords().get(0).getS3();
//
//
//        data = AmazonS3ClientBuilder
//                .standard()
//                .build()
//                .getObjectAsString(s3.getBucket().getName(),
//                        s3.getObject().getKey());
//    }

}
