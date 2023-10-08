package aws;

public record S3ObjectName(BucketNameTransformer bucketName, KeyNameTransformer keyName) implements S3Object {
     @Override
     public String getBucketName() {
         return bucketName.get();
     }

     @Override
     public String getKeyName() {
      return keyName.get();
 }
}
