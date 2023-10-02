package aws;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.amazonaws.services.s3.AmazonS3;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@MockitoSettings
class S3SingleFileDataTest {

    @Test
    void retrieveSingleFileData(@Mock final AmazonS3 amazonS3,
                                @Mock final S3Event s3Event,
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

        when(amazonS3.getObjectAsString(any(), any())).thenReturn("someData");

        when(notificationRecord.getS3()).thenReturn(s3Entity);

        final S3SingleFileData s3SingleFileData = new S3SingleFileData("expectedName.txt", ()->amazonS3);

        s3SingleFileData.retrieve(s3Event)
                .ifPresentOrElse(s->assertThat(s).contains("someData"),
                        ()->fail("Expected to have some data"));

    }

    @Test
    void retrieveSingleFileDataManyEvents(@Mock final AmazonS3 amazonS3,
                                @Mock final S3Event s3Event,
                                @Mock final S3EventNotification.S3EventNotificationRecord notificationRecord,
                                @Mock final S3EventNotification.S3Entity s3Entity,
                                @Mock final S3EventNotification.S3BucketEntity bucket,
                                @Mock final S3EventNotification.S3ObjectEntity objectEntity){

        final List<S3EventNotification.S3EventNotificationRecord> records = new ArrayList<>();
        records.add(notificationRecord);
        records.add(notificationRecord);

        when(s3Event.getRecords()).thenReturn(records);

        when(s3Entity.getBucket()).thenReturn(bucket);
        when(s3Entity.getObject()).thenReturn(objectEntity);

        when(bucket.getName()).thenReturn("name");
        when(objectEntity.getKey()).thenReturn("expectedName.txt").thenReturn("unexpected.txt");

        when(amazonS3.getObjectAsString(any(), any())).thenReturn("someData");

        when(notificationRecord.getS3()).thenReturn(s3Entity);

        final S3SingleFileData s3SingleFileData = new S3SingleFileData("expectedName.txt", ()->amazonS3);

        s3SingleFileData.retrieve(s3Event)
                .ifPresentOrElse(s->assertThat(s).contains("someData"),
                        ()->fail("Expected to have some data"));

    }

    @Test
    void retrieveSingleFileNoNameMatch(@Mock final AmazonS3 amazonS3,
                                       @Mock final S3Event s3Event,
                                       @Mock final S3EventNotification.S3EventNotificationRecord notificationRecord,
                                       @Mock final S3EventNotification.S3Entity s3Entity,
                                       @Mock final S3EventNotification.S3ObjectEntity objectEntity){

        final List<S3EventNotification.S3EventNotificationRecord> records = new ArrayList<>();
        records.add(notificationRecord);

        when(s3Event.getRecords()).thenReturn(records);

        when(s3Entity.getObject()).thenReturn(objectEntity);

        when(objectEntity.getKey()).thenReturn("unexpected.txt");

        when(notificationRecord.getS3()).thenReturn(s3Entity);

        final S3SingleFileData s3SingleFileData = new S3SingleFileData("expectedName.txt", ()->amazonS3);

        s3SingleFileData.retrieve(s3Event)
                .ifPresent(__->fail("Expected no data as there were 0 matching records"));

    }

    @Test
    void retrieveNoFileFailure(@Mock final AmazonS3 amazonS3,
                               @Mock final S3Event s3Event){

        final List<S3EventNotification.S3EventNotificationRecord> records = new ArrayList<>();

        when(s3Event.getRecords()).thenReturn(records);


        final S3SingleFileData s3SingleFileData = new S3SingleFileData("expectedName.txt", ()->amazonS3);

        s3SingleFileData.retrieve(s3Event)
                .ifPresent(__->fail("Expected no data as there were 0 matching records"));

    }

}