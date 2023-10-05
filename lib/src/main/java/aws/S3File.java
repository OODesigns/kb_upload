package aws;

public record S3File(BucketNameProvider bucketNameProvider, KeyNameProvider keyNameProvider) {}
