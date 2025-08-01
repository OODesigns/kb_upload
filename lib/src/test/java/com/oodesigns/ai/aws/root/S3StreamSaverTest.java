package com.oodesigns.ai.aws.root;
import com.oodesigns.ai.cloud.CloudObjectReference;
import com.oodesigns.ai.cloud.CloudStoreStateError;
import com.oodesigns.ai.cloud.CloudStoreStateOK;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings
class S3StreamSaverTest {
    @Test
    void savingReturnsOKState(@Mock final S3Client s3Client,
                              @Mock final CloudObjectReference cloudObjectReference,
                              @Mock final ByteArrayOutputStream streamContents){

        when(streamContents.toByteArray()).thenReturn("someData".getBytes());

        final S3StreamSaver s3StreamSaver = new S3StreamSaver(s3Client);

        assertThat(s3StreamSaver.store(cloudObjectReference, streamContents)).isInstanceOf(CloudStoreStateOK.class);

        final ArgumentCaptor<RequestBody> contents = ArgumentCaptor.forClass(RequestBody.class);

        verify(s3Client,times(1)).putObject(any(PutObjectRequest.class), contents.capture());
        try (final InputStream stream = contents.getValue().contentStreamProvider().newStream()){

            final String expectedContents = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            assertThat(expectedContents).contains("someData");

        } catch (final IOException _) {
            fail("Unexpected failure");
        }
    }

    @Test
    void errorSavingReturnsErrorState(@Mock final S3Client s3Client,
                                      @Mock final S3CloudObjectReference s3Reference,
                                      @Mock final ByteArrayOutputStream streamContents){

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class) )).thenThrow(SdkException.class);
        when(streamContents.toByteArray()).thenReturn("someData".getBytes());

        final S3StreamSaver s3StreamSaver = new S3StreamSaver(s3Client);

        assertThat(s3StreamSaver.store(s3Reference, streamContents)).isInstanceOf(CloudStoreStateError.class);

    }

}