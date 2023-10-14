package aws;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.when;


@MockitoSettings
class S3EventSingleObjectTest {

    @Test
    void retrieveSingleS3Object(@Mock final S3Event s3Event,
                                @Mock final S3EventNotification.S3EventNotificationRecord notificationRecord,
                                @Mock final S3EventNotification.S3Entity s3Entity,
                                @Mock final S3EventNotification.S3BucketEntity bucket,
                                @Mock final S3EventNotification.S3ObjectEntity objectEntity){

        final List<S3EventNotification.S3EventNotificationRecord> records = new ArrayList<>();
        records.add(notificationRecord);

        when(s3Event.getRecords()).thenReturn(records);

        when(s3Entity.getBucket()).thenReturn(bucket);
        when(s3Entity.getObject()).thenReturn(objectEntity);

        when(bucket.getName()).thenReturn("name");
        when(objectEntity.getKey()).thenReturn("expectedName.txt");

        when(notificationRecord.getS3()).thenReturn(s3Entity);

        final S3EventSingleObject s3EventSingleObject = new S3EventSingleObject("expectedName.txt",
                BucketName::new, KeyName::new);

        s3EventSingleObject.retrieve(s3Event)
                .ifPresentOrElse(s->{
                    assertThat(s.getBucketName()).contains("name");
                    assertThat(s.getKeyName()).contains("expectedName.txt");},
                        ()->fail("Expected to have an object"));

    }

    @Test
    void retrieveSingleS3ObjectFromPath(@Mock final S3Event s3Event,
                                @Mock final S3EventNotification.S3EventNotificationRecord notificationRecord,
                                @Mock final S3EventNotification.S3Entity s3Entity,
                                @Mock final S3EventNotification.S3BucketEntity bucket,
                                @Mock final S3EventNotification.S3ObjectEntity objectEntity){

        final List<S3EventNotification.S3EventNotificationRecord> records = new ArrayList<>();
        records.add(notificationRecord);

        when(s3Event.getRecords()).thenReturn(records);

        when(s3Entity.getBucket()).thenReturn(bucket);
        when(s3Entity.getObject()).thenReturn(objectEntity);

        when(bucket.getName()).thenReturn("name");
        when(objectEntity.getKey()).thenReturn("folder1/folder2/expectedName.txt");

        when(notificationRecord.getS3()).thenReturn(s3Entity);

        final S3EventSingleObject s3EventSingleObject = new S3EventSingleObject("expectedName.txt",
                BucketName::new, KeyName::new);

        s3EventSingleObject.retrieve(s3Event)
                .ifPresentOrElse(s->{
                            assertThat(s.getBucketName()).contains("name");
                            assertThat(s.getKeyName()).contains("expectedName.txt");},
                        ()->fail("Expected to have an object"));

    }

    @Test
    void retrieveSingleFileDataManyEvents(@Mock final S3Event s3Event,
                                          @Mock final S3EventNotification.S3EventNotificationRecord notificationRecord,
                                          @Mock final S3EventNotification.S3EventNotificationRecord notificationRecord2,
                                          @Mock final S3EventNotification.S3Entity s3Entity,
                                          @Mock final S3EventNotification.S3Entity s3Entity2,
                                          @Mock final S3EventNotification.S3BucketEntity bucket,
                                          @Mock final S3EventNotification.S3ObjectEntity objectEntity,
                                          @Mock final S3EventNotification.S3ObjectEntity objectEntity2){

        final List<S3EventNotification.S3EventNotificationRecord> records = new ArrayList<>();
        records.add(notificationRecord);
        records.add(notificationRecord2);

        when(s3Event.getRecords()).thenReturn(records);

        when(s3Entity.getBucket()).thenReturn(bucket);
        when(s3Entity.getObject()).thenReturn(objectEntity);

        when(bucket.getName()).thenReturn("name");
        when(objectEntity.getKey()).thenReturn("expectedName.txt");

        when(s3Entity2.getObject()).thenReturn(objectEntity2);
        when(objectEntity2.getKey()).thenReturn("unexpectedName2.txt");

        when(notificationRecord.getS3()).thenReturn(s3Entity);
        when(notificationRecord2.getS3()).thenReturn(s3Entity2);

        final S3EventSingleObject s3EventSingleObject = new S3EventSingleObject("expectedName.txt",
                BucketName::new, KeyName::new);

        s3EventSingleObject.retrieve(s3Event)
                .ifPresentOrElse(s->{
                            assertThat(s.getBucketName()).contains("name");
                            assertThat(s.getKeyName()).contains("expectedName.txt");},
                        ()->fail("Expected to have an object"));

    }

    @Test
    void retrieveSingleFileNoNameMatch(@Mock final S3Event s3Event,
                                       @Mock final S3EventNotification.S3EventNotificationRecord notificationRecord,
                                       @Mock final S3EventNotification.S3Entity s3Entity,
                                       @Mock final S3EventNotification.S3ObjectEntity objectEntity){

        final List<S3EventNotification.S3EventNotificationRecord> records = new ArrayList<>();
        records.add(notificationRecord);

        when(s3Event.getRecords()).thenReturn(records);

        when(s3Entity.getObject()).thenReturn(objectEntity);

        when(objectEntity.getKey()).thenReturn("unexpected.txt");

        when(notificationRecord.getS3()).thenReturn(s3Entity);

        final S3EventSingleObject s3EventSingleObject = new S3EventSingleObject("expectedName.txt",
                BucketName::new, KeyName::new);

        s3EventSingleObject.retrieve(s3Event)
                .ifPresent(__->fail("Expected no object as there were 0 matching records"));

    }

    @Test
    void retrieveNoFileFailure(@Mock final S3Event s3Event){

        final List<S3EventNotification.S3EventNotificationRecord> records = new ArrayList<>();

        when(s3Event.getRecords()).thenReturn(records);


        final S3EventSingleObject s3EventSingleObject = new S3EventSingleObject("expectedName.txt",
                BucketName::new, KeyName::new);

        s3EventSingleObject.retrieve(s3Event)
                .ifPresent(__->fail("Expected no object as there were 0 matching records"));

    }

}