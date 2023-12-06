package aws;
import cloud.*;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import general.Mappable;
import general.Transformer;
import json.JSON;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import com.amazonaws.services.lambda.runtime.Context;
import support.LogCapture;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings
class HandleTransformationTest {
    public static final String HANDLE_RESULT = "assistant_configuration_creator.HandleResult";

    @Test
    void errorExpectedTransformationBucketNameNullParameters(
                                       @Mock final Context context,
                                       @Mock final Transformer<JSON, Mappable<List<String>, String, String>> jsonTransformer,
                                       @Mock final CloudStorable fileStore,
                                       @Mock final CloudCopyable cloudCopyable,
                                       @Mock final CloudLoadable<String> cloudLoadable){

        final HandleTransformation handleTransformation
                = new HandleTransformation(
                        cloudLoadable,
                        jsonTransformer,
                        fileStore,
                        cloudCopyable);

        final CloudException cloudException =
                assertThrows(CloudException.class, () -> handleTransformation.handleRequest(null, context));

        assertThat(cloudException.getMessage()).contains("Bucket name for transformation file is missing");
    }

    @Test
    void errorExpectedTransformationBucketNameMissing(@Mock final Context context,
                                                      @Mock final Transformer<JSON, Mappable<List<String>, String, String>> jsonTransformer,
                                              @Mock final CloudStorable fileStore,
                                              @Mock final CloudCopyable cloudCopyable,
                                              @Mock final CloudLoadable<String> cloudLoadable){

        final Map<String, String> input = Map.of("wrong Key", "wrong value");

        final HandleTransformation handleTransformation
                = new HandleTransformation(
                        cloudLoadable,
                        jsonTransformer,
                        fileStore,
                        cloudCopyable);

        final CloudException cloudException = assertThrows(CloudException.class, () -> handleTransformation.handleRequest(input, context));

        assertThat(cloudException.getMessage()).contains("Bucket name for transformation file is missing");
    }

    @Test
    void errorExpectedTransformationBucketNameMissingData(@Mock final Context context,
                                              @Mock final LambdaLogger lambdaLogger,
                                              @Mock final Transformer<JSON, Mappable<List<String>, String, String>> jsonTransformer,
                                              @Mock final CloudStorable fileStore,
                                              @Mock final CloudCopyable cloudCopyable,
                                              @Mock final CloudLoadable<String> cloudLoadable){



        when(context.getLogger()).thenReturn(lambdaLogger);
        when(cloudLoadable.retrieve(any(), any())).thenReturn(Optional.of("{}"));

        final Map<String, String> input = Map.of("Transformation-BucketName", "");

        final HandleTransformation handleTransformation
                = new HandleTransformation(
                        cloudLoadable,
                        jsonTransformer,
                        fileStore,
                        cloudCopyable);

        assertThrows(CloudException.class, ()->handleTransformation.handleRequest(input, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("Bucket name for transformation file is missing");
    }

    @Test
    void errorExpectedTransformationKeyNameMissing(@Mock final Context context,
                                                   @Mock final LambdaLogger lambdaLogger,
                                                   @Mock final Transformer<JSON, Mappable<List<String>, String, String>> jsonTransformer,
                                                   @Mock final CloudStorable fileStore,
                                                   @Mock final CloudCopyable cloudCopyable,
                                                   @Mock final CloudLoadable<String> cloudLoadable){


        when(context.getLogger()).thenReturn(lambdaLogger);

        final Map<String, String> input = Map.of("Transformation-BucketName", "bucket");
        when(cloudLoadable.retrieve(any(), any())).thenReturn(Optional.of("{}"));

        final HandleTransformation handleTransformation
                = new HandleTransformation(
                        cloudLoadable,
                        jsonTransformer,
                        fileStore,
                        cloudCopyable);

        assertThrows(CloudException.class, ()->handleTransformation.handleRequest(input, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("Key name for transformation file is missing");
    }

    @Test
    void errorUnableTransformData(@Mock final Context context,
                               @Mock final LambdaLogger lambdaLogger,
                               @Mock final Transformer<JSON, Mappable<List<String>, String, String>> jsonTransformer,
                               @Mock final Mappable<List<String>, String, String> transformedResult,
                               @Mock final CloudStorable fileStore,
                               @Mock final CloudCopyable cloudCopyable,
                               @Mock final CloudLoadable<String> cloudLoadable){


        when(context.getLogger()).thenReturn(lambdaLogger);
        when(jsonTransformer.transform(any())).thenReturn(transformedResult);
        when(transformedResult.map(any())).thenReturn(Optional.empty());
        when(cloudLoadable.retrieve(any(), any())).thenReturn(Optional.of("{}"));

        final Map<String, String> input = Map.of("Transformation-BucketName", "bucket",
                                                 "Transformation-KeyName", "key");

        final HandleTransformation handleTransformation
                = new HandleTransformation(
                        cloudLoadable,
                        jsonTransformer,
                        fileStore,
                        cloudCopyable);

        assertThrows(CloudException.class, ()->handleTransformation.handleRequest(input, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("Unable to transform data");
    }

    @Test
    void errorBucketNameForTransFormedFileIsMissing
                                  (@Mock final Context context,
                                   @Mock final LambdaLogger lambdaLogger,
                                   @Mock final Transformer<JSON, Mappable<List<String>, String, String>> jsonTransformer,
                                   @Mock final Mappable<List<String>, String, String> transformedResult,
                                   @Mock final CloudStorable fileStore,
                                   @Mock final CloudCopyable cloudCopyable,
                                   @Mock final CloudLoadable<String> cloudLoadable){


        when(context.getLogger()).thenReturn(lambdaLogger);
        when(jsonTransformer.transform(any())).thenReturn(transformedResult);
        when(transformedResult.map(any())).thenReturn(Optional.of(List.of("data1", "data2").toString()));
        when(cloudLoadable.retrieve(any(), any())).thenReturn(Optional.of("{}"));

        final Map<String, String> input = Map.of("Transformation-BucketName", "bucket",
                                                 "Transformation-KeyName", "key");

        final HandleTransformation handleTransformation
                = new HandleTransformation(
                        cloudLoadable,
                        jsonTransformer,
                        fileStore,
                        cloudCopyable);

        assertThrows(CloudException.class, ()->handleTransformation.handleRequest(input, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("Bucket name for transformed file is missing");
    }

    @Test
    void errorKeyNameForTransFormedIsMissing
            (@Mock final Context context,
             @Mock final Transformer<JSON, Mappable<List<String>, String, String>> jsonTransformer,
             @Mock final Mappable<List<String>, String, String> transformedResult,
             @Mock final CloudStorable fileStore,
             @Mock final CloudCopyable cloudCopyable,
             @Mock final CloudLoadable<String> cloudLoadable){

        when(jsonTransformer.transform(any())).thenReturn(transformedResult);
        when(transformedResult.map(any())).thenReturn(Optional.of(List.of("data1", "data2").toString()));
        when(cloudLoadable.retrieve(any(), any())).thenReturn(Optional.of("{}"));

        final Map<String, String> input = Map.of("Transformation-BucketName", "bucket1",
                                                 "Transformation-KeyName", "key1",
                                                 "Transformed-BucketName", "bucket2");

        final HandleTransformation handleTransformation
                = new HandleTransformation(
                        cloudLoadable,
                        jsonTransformer,
                        fileStore,
                        cloudCopyable);

        final CloudException cloudException
                = assertThrows(CloudException.class, () -> handleTransformation.handleRequest(input, context));

        assertThat(cloudException.getMessage()).contains("Key name for transformed file is missing");
    }

    @Test
    void errorSavingFile
            (@Mock final Context context,
             @Mock final Transformer<JSON, Mappable<List<String>, String, String>> jsonTransformer,
             @Mock final Mappable<List<String>, String, String> transformedResult,
             @Mock final CloudStorable cloudStorable,
             @Mock final CloudCopyable cloudCopyable,
             @Mock final CloudLoadable<String> cloudLoadable){

        when(jsonTransformer.transform(any())).thenReturn(transformedResult);
        when(transformedResult.map(any())).thenReturn(Optional.of(List.of("data1", "data2").toString()));
        when(cloudStorable.store(any(), any())).thenReturn(new CloudStoreStateError("Test Error"));
        when(cloudLoadable.retrieve(any(), any())).thenReturn(Optional.of("{}"));

        final Map<String, String> input = Map.of(
                "Transformation-BucketName", "bucket1",
                "Transformation-KeyName", "key1",
                "Transformed-BucketName", "bucket2",
                "Transformed-KeyName", "key2");


        final HandleTransformation handleTransformation
                = new HandleTransformation(
                        cloudLoadable,
                        jsonTransformer,
                        cloudStorable,
                        cloudCopyable);

        final CloudException cloudException = assertThrows(CloudException.class, () -> handleTransformation.handleRequest(input, context));

        assertThat(cloudException.getMessage()).contains("Test Error");
    }

    @Test
    void SavingFileWithOutIssue
            (@Mock final Context context,
             @Mock final Transformer<JSON, Mappable<List<String>, String, String>> jsonTransformer,
             @Mock final Mappable<List<String>, String, String> transformedResult,
             @Mock final CloudStorable fileStore,
             @Mock final CloudCopyable cloudCopyable,
             @Mock final CloudLoadable<String> cloudLoadable){


        when(jsonTransformer.transform(any())).thenReturn(transformedResult);
        when(transformedResult.map(any())).thenReturn(Optional.of(List.of("data1", "data2").toString()));
        when(fileStore.store(any(), any())).thenReturn(new CloudStoreStateOK());
        when(cloudLoadable.retrieve(any(), any())).thenReturn(Optional.of("{}"));

        final Map<String, String> input = Map.of(
                "Transformation-BucketName", "bucket1",
                "Transformation-KeyName", "key1",
                "Transformed-BucketName", "bucket2",
                "Transformed-KeyName", "key2");

        final HandleTransformation handleTransformation
                = new HandleTransformation(
                        cloudLoadable,
                        jsonTransformer,
                        fileStore,
                        cloudCopyable);

        try(final LogCapture logData =new LogCapture(HANDLE_RESULT)) {

            handleTransformation.handleRequest(input, context);


            assertThat(logData.getLogs().get(0).getMessage()).contains("S3FileSaverOKState");
        }
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

        assertThrows(CloudException.class, ()-> handleTransformation.handleRequest(input, context));
    }

}