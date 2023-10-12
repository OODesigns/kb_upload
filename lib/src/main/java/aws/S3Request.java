package aws;

import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class S3Request implements S3RequestProvider {

    @Override
    public PutObjectRequest getPutRequest(final S3Object s3object) {
        return PutObjectRequest.builder()
                .bucket(s3object.getBucketName())
                .key(s3object.getKeyName())
                .build();
    }

    @Override
    public GetObjectRequest getGetRequest(final S3Object s3object) {
        return GetObjectRequest.builder()
                .bucket(s3object.getBucketName())
                .key(s3object.getKeyName())
                .build();
    }
}
