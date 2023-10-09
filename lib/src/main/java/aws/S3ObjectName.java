package aws;

public record S3ObjectName(BucketNameProvider bucketName, KeyNameProvider keyName) implements S3Object {
     @Override
     public String getBucketName() {
         return bucketName.get();
     }

     @Override
     public String getKeyName() {
      return keyName.get();
 }
}
