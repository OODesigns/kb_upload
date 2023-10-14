package aws;

import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public interface S3RequestProvider {
    PutObjectRequest getPutRequest(S3Object s3Object);

    GetObjectRequest getGetRequest(S3Object s3Object);
}
