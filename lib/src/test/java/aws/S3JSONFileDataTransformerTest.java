package aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import kb_upload.JSON;
import kb_upload.Retrievable;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@MockitoSettings
class S3JSONFileDataTransformerTest {
    @Test
    public void testTransform_successfullyTransforms(
            final @Mock S3Object s3Object,
            final @Mock Retrievable<S3Object, Optional<String>> fileLoaderMock,
            final @Mock Context context) {

        when(fileLoaderMock.retrieve(s3Object)).thenReturn(Optional.of("{\"key\": \"value\"}"));

        final S3JSONFileDataTransformer transformer = new S3JSONFileDataTransformer(fileLoaderMock);

        final JSON result = transformer.transform(context, s3Object);

        assertThat(result.get()).contains("{\"key\": \"value\"}");
    }

    @Test
    public void testTransform_throwsExceptionWhenFileNotLoaded(
            final @Mock S3Object s3Object,
            final @Mock Retrievable<S3Object, Optional<String>> fileLoaderMock,
            final @Mock Context context,
            final @Mock LambdaLogger lambdaLogger) {

        when(s3Object.getBucketName()).thenReturn("sample-bucket");
        when(s3Object.getKeyName()).thenReturn("sample-key");
        when(context.getLogger()).thenReturn(lambdaLogger);

        when(fileLoaderMock.retrieve(s3Object)).thenReturn(Optional.empty());

        final S3JSONFileDataTransformer transformer = new S3JSONFileDataTransformer(fileLoaderMock);

        final s3Exception exception = assertThrows(s3Exception.class, () -> transformer.transform(context, s3Object));

        assertEquals("Unable to load file from bucket: sample-bucket and key: sample-key", exception.getMessage());
    }

}