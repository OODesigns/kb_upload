package aws;

import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class S3Request implements S3RequestProvider {

    @Override
    public PutObjectRequest getPutRequest(final S3Object s3Object) {
        return PutObjectRequest.builder()
                .bucket(s3Object.getBucketName())
                .key(s3Object.getKeyName())
                .build();
    }

    @Override
    public GetObjectRequest getGetRequest(final S3Object s3Object) {
        return GetObjectRequest.builder()
                .bucket(s3Object.getBucketName())
                .key(s3Object.getKeyName())
                .build();
    }
}
