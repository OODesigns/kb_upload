package aws;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import kb_upload.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import com.amazonaws.services.lambda.runtime.Context;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings
class HandleTransformationTest {

    @Test
    void errorExpectedTransformationBucketNameNullParameters(
                                       @Mock final Context context,
                                       @Mock final LambdaLogger lambdaLogger,
                                       @Mock final Transformer1_1<JSON, mappable<List<String>, String, String>> jsonTransformer,
                                       @Mock final Storable<S3Object, ByteArrayOutputStream, S3FileSaverState> fileStore,
                                       @Mock final S3RequestProvider s3RequestProvider){

        when(context.getLogger()).thenReturn(lambdaLogger);

        final HandleTransformation handleTransformation
                = new HandleTransformation(
                        __-> Optional.of(new ByteArrayInputStream("{}".getBytes())),
                        jsonTransformer,
                        fileStore,
                        s3RequestProvider);

        assertThrows(s3Exception.class, ()->handleTransformation.handleRequest(null, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("Bucket name for transformation file is missing");
    }

    @Test
    void errorExpectedTransformationBucketNameMissing(@Mock final Context context,
                                              @Mock final LambdaLogger lambdaLogger,
                                              @Mock final Transformer1_1<JSON, mappable<List<String>, String, String>> jsonTransformer,
                                              @Mock final Storable<S3Object, ByteArrayOutputStream, S3FileSaverState> fileStore,
                                              @Mock final S3RequestProvider s3RequestProvider){

        when(context.getLogger()).thenReturn(lambdaLogger);

        final Map<String, String> input = Map.of("wrong Key", "wrong value");

        final HandleTransformation handleTransformation
                = new HandleTransformation(
                        __-> Optional.of(new ByteArrayInputStream("{}".getBytes())),
                        jsonTransformer,
                        fileStore,
                        s3RequestProvider);

        assertThrows(s3Exception.class, ()->handleTransformation.handleRequest(input, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("Bucket name for transformation file is missing");
    }

    @Test
    void errorExpectedTransformationBucketNameMissingData(@Mock final Context context,
                                              @Mock final LambdaLogger lambdaLogger,
                                              @Mock final Transformer1_1<JSON, mappable<List<String>, String, String>> jsonTransformer,
                                              @Mock final Storable<S3Object, ByteArrayOutputStream, S3FileSaverState> fileStore,
                                              @Mock final S3RequestProvider s3RequestProvider){

        when(context.getLogger()).thenReturn(lambdaLogger);

        final Map<String, String> input = Map.of("Transformation-BucketName", "");

        final HandleTransformation handleTransformation
                = new HandleTransformation(
                        __-> Optional.of(new ByteArrayInputStream("{}".getBytes())),
                        jsonTransformer,
                        fileStore,
                        s3RequestProvider);

        assertThrows(s3Exception.class, ()->handleTransformation.handleRequest(input, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("Bucket name for transformation file is missing");
    }

    @Test
    void errorExpectedTransformationKeyNameMissing(@Mock final Context context,
                                                   @Mock final LambdaLogger lambdaLogger,
                                                   @Mock final Transformer1_1<JSON, mappable<List<String>, String, String>> jsonTransformer,
                                                   @Mock final Storable<S3Object, ByteArrayOutputStream, S3FileSaverState> fileStore,
                                                   @Mock final S3RequestProvider s3RequestProvider){

        when(context.getLogger()).thenReturn(lambdaLogger);

        final Map<String, String> input = Map.of("Transformation-BucketName", "bucket");

        final HandleTransformation handleTransformation
                = new HandleTransformation(
                        __-> Optional.of(new ByteArrayInputStream("{}".getBytes())),
                        jsonTransformer,
                        fileStore,
                        s3RequestProvider);

        assertThrows(s3Exception.class, ()->handleTransformation.handleRequest(input, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("Key name for transformation file is missing");
    }

    @Test
    void errorUnableTransformData(@Mock final Context context,
                               @Mock final LambdaLogger lambdaLogger,
                               @Mock final Transformer1_1<JSON, mappable<List<String>, String, String>> jsonTransformer,
                               @Mock final mappable<List<String>, String, String>  transformedResult,
                               @Mock final Storable<S3Object, ByteArrayOutputStream, S3FileSaverState> fileStore,
                               @Mock final S3RequestProvider s3RequestProvider){

        when(context.getLogger()).thenReturn(lambdaLogger);
        when(jsonTransformer.transform(any())).thenReturn(transformedResult);
        when(transformedResult.map(any())).thenReturn(Optional.empty());

        final Map<String, String> input = Map.of("Transformation-BucketName", "bucket",
                                                 "Transformation-KeyName", "key");

        final HandleTransformation handleTransformation
                = new HandleTransformation(
                        __-> Optional.of(new ByteArrayInputStream("{}".getBytes())),
                        jsonTransformer,
                        fileStore,
                        s3RequestProvider);

        assertThrows(s3Exception.class, ()->handleTransformation.handleRequest(input, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("Unable to transform data");
    }

    @Test
    void errorBucketNameForTransFormedFileIsMissing
                                  (@Mock final Context context,
                                   @Mock final LambdaLogger lambdaLogger,
                                   @Mock final Transformer1_1<JSON, mappable<List<String>, String, String>> jsonTransformer,
                                   @Mock final mappable<List<String>, String, String>  transformedResult,
                                   @Mock final Storable<S3Object, ByteArrayOutputStream, S3FileSaverState> fileStore,
                                   @Mock final S3RequestProvider s3RequestProvider){

        when(context.getLogger()).thenReturn(lambdaLogger);
        when(jsonTransformer.transform(any())).thenReturn(transformedResult);
        when(transformedResult.map(any())).thenReturn(Optional.of(List.of("data1", "data2").toString()));

        final Map<String, String> input = Map.of("Transformation-BucketName", "bucket",
                                                 "Transformation-KeyName", "key");

        final HandleTransformation handleTransformation
                = new HandleTransformation(
                        __-> Optional.of(new ByteArrayInputStream("{}".getBytes())),
                        jsonTransformer,
                        fileStore,
                        s3RequestProvider);

        assertThrows(s3Exception.class, ()->handleTransformation.handleRequest(input, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("Bucket name for transformed file is missing");
    }

    @Test
    void errorKeyNameForTransFormedIsMissing
            (@Mock final Context context,
             @Mock final LambdaLogger lambdaLogger,
             @Mock final Transformer1_1<JSON, mappable<List<String>, String, String>> jsonTransformer,
             @Mock final mappable<List<String>, String, String>  transformedResult,
             @Mock final Storable<S3Object, ByteArrayOutputStream, S3FileSaverState> fileStore,
             @Mock final S3RequestProvider s3RequestProvider){

        when(context.getLogger()).thenReturn(lambdaLogger);
        when(jsonTransformer.transform(any())).thenReturn(transformedResult);
        when(transformedResult.map(any())).thenReturn(Optional.of(List.of("data1", "data2").toString()));

        final Map<String, String> input = Map.of("Transformation-BucketName", "bucket1",
                                                 "Transformation-KeyName", "key1",
                                                 "Transformed-BucketName", "bucket2");

        final HandleTransformation handleTransformation
                = new HandleTransformation(
                        __-> Optional.of(new ByteArrayInputStream("{}".getBytes())),
                        jsonTransformer,
                        fileStore,
                        s3RequestProvider);

        assertThrows(s3Exception.class, ()->handleTransformation.handleRequest(input, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("Key name for transformed file is missing");
    }

    @Test
    void errorSavingFile
            (@Mock final Context context,
             @Mock final LambdaLogger lambdaLogger,
             @Mock final Transformer1_1<JSON, mappable<List<String>, String, String>> jsonTransformer,
             @Mock final mappable<List<String>, String, String>  transformedResult,
             @Mock final Storable<S3Object, ByteArrayOutputStream, S3FileSaverState> fileStore,
             @Mock final S3RequestProvider s3RequestProvider){

        when(context.getLogger()).thenReturn(lambdaLogger);
        when(jsonTransformer.transform(any())).thenReturn(transformedResult);
        when(transformedResult.map(any())).thenReturn(Optional.of(List.of("data1", "data2").toString()));
        when(fileStore.store(any(), any())).thenReturn(new S3FileSaverErrorState("Test Error"));

        final Map<String, String> input = Map.of(
                "Transformation-BucketName", "bucket1",
                "Transformation-KeyName", "key1",
                "Transformed-BucketName", "bucket2",
                "Transformed-KeyName", "key2");


        final HandleTransformation handleTransformation
                = new HandleTransformation(
                        __-> Optional.of(new ByteArrayInputStream("{}".getBytes())),
                        jsonTransformer,
                        fileStore,
                        s3RequestProvider);

        assertThrows(s3Exception.class, ()->handleTransformation.handleRequest(input, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("Test Error");
    }

    @Test
    void SavingFileWithOutIssue
            (@Mock final Context context,
             @Mock final LambdaLogger lambdaLogger,
             @Mock final Transformer1_1<JSON, mappable<List<String>, String, String>> jsonTransformer,
             @Mock final mappable<List<String>, String, String>  transformedResult,
             @Mock final Storable<S3Object, ByteArrayOutputStream, S3FileSaverState> fileStore,
             @Mock final S3RequestProvider s3RequestProvider){

        when(context.getLogger()).thenReturn(lambdaLogger);
        when(jsonTransformer.transform(any())).thenReturn(transformedResult);
        when(transformedResult.map(any())).thenReturn(Optional.of(List.of("data1", "data2").toString()));
        when(fileStore.store(any(), any())).thenReturn(new S3FileSaverOKState());

        final Map<String, String> input = Map.of(
                "Transformation-BucketName", "bucket1",
                "Transformation-KeyName", "key1",
                "Transformed-BucketName", "bucket2",
                "Transformed-KeyName", "key2");

        final HandleTransformation handleTransformation
                = new HandleTransformation(
                        __-> Optional.of(new ByteArrayInputStream("{}".getBytes())),
                        jsonTransformer,
                        fileStore,
                        s3RequestProvider);

        handleTransformation.handleRequest(input, context);

        final ArgumentCaptor<ByteArrayOutputStream> data = ArgumentCaptor.forClass(ByteArrayOutputStream.class);
        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);

        verify(fileStore, times(1)).store(any(), data.capture());
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("S3FileSaverOKState");
    }

    @Test
    void handleTransformationWithDefaultConNoValidFile(@Mock final Context context,
                                                       @Mock final LambdaLogger lambdaLogger){

        final Map<String, String> input = Map.of(
                "Transformation-BucketName", "bucket1",
                "Transformation-KeyName", "key1",
                "Transformed-BucketName", "bucket2",
                "Transformed-KeyName", "key2");

        when(context.getLogger()).thenReturn(lambdaLogger);


        final HandleTransformation handleTransformation
                = new HandleTransformation();

        assertThrows(s3Exception.class, ()-> handleTransformation.handleRequest(input, context));
    }

}