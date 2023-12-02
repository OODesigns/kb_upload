package cloud;

import aws.root.AWSS3Exception;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import json.JSON;
import general.Retrievable;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@MockitoSettings
class CloudJSONFileDataTransformerTest {
    @Test
    public void testTransform_successfullyTransforms(
            final @Mock CloudObjectReference cloudObjectReference,
            final @Mock Retrievable<CloudObjectReference, Optional<InputStream>> fileLoaderMock){

        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream("{\"key\": \"value\"}".getBytes());

        when(fileLoaderMock.retrieve(cloudObjectReference)).thenReturn(Optional.of(byteArrayInputStream));

        final CloudJSONFileDataTransformer cloudJSONFileDataTransformer = new CloudJSONFileDataTransformer(new CloudLoad<>(fileLoaderMock));

        final JSON result = cloudJSONFileDataTransformer.transform(cloudObjectReference);

        assertThat(result.get()).contains("{\"key\": \"value\"}");
    }

    @Test
    public void testTransform_throwsExceptionWhenFileNotLoaded(
            final @Mock CloudObjectReference cloudObjectReference,
            final @Mock Retrievable<CloudObjectReference, Optional<InputStream>> fileLoaderMock,
            final @Mock Context context,
            final @Mock LambdaLogger lambdaLogger) {

        when(cloudObjectReference.getStoreName()).thenReturn("sample-bucket");
        when(cloudObjectReference.getObjectName()).thenReturn("sample-key");
        when(context.getLogger()).thenReturn(lambdaLogger);

        when(fileLoaderMock.retrieve(cloudObjectReference)).thenReturn(Optional.empty());

        final CloudJSONFileDataTransformer cloudJSONFileDataTransformer = new CloudJSONFileDataTransformer(new CloudLoad<>(fileLoaderMock));

        final AWSS3Exception exception = assertThrows(AWSS3Exception.class, () ->
                cloudJSONFileDataTransformer.transform(cloudObjectReference));

        assertEquals("Unable to load file from bucket: sample-bucket and key: sample-key", exception.getMessage());
    }

}