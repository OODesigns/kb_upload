package aws;

public record S3Object(BucketNameTransformer bucketNameTransformer, KeyNameTransformer keyNameTransformer) {}
