package aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import kb_upload.JSON;
import kb_upload.Retrievable;
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
class S3JSONFileDataTransformerTest {
    @Test
    public void testTransform_successfullyTransforms(
            final @Mock S3ObjectReference s3ObjectReference,
            final @Mock Retrievable<S3ObjectReference, Optional<InputStream>> fileLoaderMock,
            final @Mock Context context){

        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream("{\"key\": \"value\"}".getBytes());

        when(fileLoaderMock.retrieve(s3ObjectReference)).thenReturn(Optional.of(byteArrayInputStream));

        final S3JSONFileDataTransformer transformer = new S3JSONFileDataTransformer(fileLoaderMock);

        final JSON result = transformer.transform(context, s3ObjectReference);

        assertThat(result.get()).contains("{\"key\": \"value\"}");
    }

    @Test
    public void testTransform_throwsExceptionWhenFileNotLoaded(
            final @Mock S3ObjectReference s3ObjectReference,
            final @Mock Retrievable<S3ObjectReference, Optional<InputStream>> fileLoaderMock,
            final @Mock Context context,
            final @Mock LambdaLogger lambdaLogger) {

        when(s3ObjectReference.getBucketName()).thenReturn("sample-bucket");
        when(s3ObjectReference.getKeyName()).thenReturn("sample-key");
        when(context.getLogger()).thenReturn(lambdaLogger);

        when(fileLoaderMock.retrieve(s3ObjectReference)).thenReturn(Optional.empty());

        final S3JSONFileDataTransformer transformer = new S3JSONFileDataTransformer(fileLoaderMock);

        final AWSS3Exception exception = assertThrows(AWSS3Exception.class, () -> transformer.transform(context, s3ObjectReference));

        assertEquals("Unable to load file from bucket: sample-bucket and key: sample-key", exception.getMessage());
    }

}