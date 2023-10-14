package aws;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.function.Supplier;

public class S3RequestSupplier extends S3ClientSupplier{
    protected final S3RequestProvider s3RequestProvider;

    public S3RequestSupplier(final Supplier<S3Client> s3Client, final S3RequestProvider s3RequestProvider) {
        super(s3Client);
        this.s3RequestProvider = s3RequestProvider;
    }

    protected PutObjectRequest getPutRequest(final S3Object s3Object){
        return s3RequestProvider.getPutRequest(s3Object);
    }

    protected GetObjectRequest getGetRequest(final S3Object s3Object){
        return s3RequestProvider.getGetRequest(s3Object);
    }
}
