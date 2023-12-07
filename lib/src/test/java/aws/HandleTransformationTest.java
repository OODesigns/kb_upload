package aws;
import cloud.*;
import general.Mappable;
import general.Transformer;
import json.JSON;
import org.junit.jupiter.api.Test;
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
                                                          @Mock final Transformer<JSON, Mappable<List<String>, String, String>> jsonTransformer,
                                                          @Mock final CloudStorable fileStore,
                                                          @Mock final CloudCopyable cloudCopyable,
                                                          @Mock final CloudLoadable<String> cloudLoadable){

        final Map<String, String> input = Map.of("Transformation-BucketName", "");

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
    void errorExpectedTransformationKeyNameMissing(@Mock final Context context,
                                                   @Mock final Transformer<JSON, Mappable<List<String>, String, String>> jsonTransformer,
                                                   @Mock final CloudStorable fileStore,
                                                   @Mock final CloudCopyable cloudCopyable,
                                                   @Mock final CloudLoadable<String> cloudLoadable){


        final Map<String, String> input = Map.of("Transformation-BucketName", "bucket");

        final HandleTransformation handleTransformation
                = new HandleTransformation(
                        cloudLoadable,
                        jsonTransformer,
                        fileStore,
                        cloudCopyable);

        final CloudException cloudException = assertThrows(CloudException.class, () -> handleTransformation.handleRequest(input, context));

        assertThat(cloudException.getMessage()).contains("Key name for transformation file is missing");
    }

    @Test
    void errorUnableTransformData(@Mock final Context context,
                                  @Mock final Transformer<JSON, Mappable<List<String>, String, String>> jsonTransformer,
                                  @Mock final Mappable<List<String>, String, String> transformedResult,
                                  @Mock final CloudStorable fileStore,
                                  @Mock final CloudCopyable cloudCopyable,
                                  @Mock final CloudLoadable<String> cloudLoadable){

        when(jsonTransformer.transform(any())).thenReturn(transformedResult);
        when(transformedResult.map(any())).thenReturn(Optional.empty());
        when(cloudLoadable.retrieve(any(), any())).thenReturn(Optional.of("{}"));

        final Map<String, String> input = Map.of("Transformation-BucketName", "bucket",
                                                 "Transformation-KeyName", "key",
                                                 "Transformed-KeyName", "key",
                                                 "Transformed-BucketName", "bucket");

        final HandleTransformation handleTransformation
                = new HandleTransformation(
                        cloudLoadable,
                        jsonTransformer,
                        fileStore,
                        cloudCopyable);

        final CloudException cloudException = assertThrows(CloudException.class, () -> handleTransformation.handleRequest(input, context));


        assertThat(cloudException.getMessage()).contains("Unable to transform data");
    }

    @Test
    void errorBucketNameForTransFormedFileIsMissing
                                  (@Mock final Context context,
                                   @Mock final Transformer<JSON, Mappable<List<String>, String, String>> jsonTransformer,
                                   @Mock final CloudStorable fileStore,
                                   @Mock final CloudCopyable cloudCopyable,
                                   @Mock final CloudLoadable<String> cloudLoadable){


        final Map<String, String> input = Map.of("Transformation-BucketName", "bucket",
                                                 "Transformation-KeyName", "key");

        final HandleTransformation handleTransformation
                = new HandleTransformation(
                        cloudLoadable,
                        jsonTransformer,
                        fileStore,
                        cloudCopyable);

        final CloudException cloudException = assertThrows(CloudException.class, () -> handleTransformation.handleRequest(input, context));

        assertThat(cloudException.getMessage()).contains("Bucket name for transformed file is missing");
    }

    @Test
    void errorKeyNameForTransFormedIsMissing
            (@Mock final Context context,
             @Mock final Transformer<JSON, Mappable<List<String>, String, String>> jsonTransformer,
             @Mock final CloudStorable fileStore,
             @Mock final CloudCopyable cloudCopyable,
             @Mock final CloudLoadable<String> cloudLoadable){

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
             @Mock final CloudCopyable cloudCopyable,
             @Mock final CloudLoadable<String> cloudLoadable,
             @Mock final CloudStorable fileStore){

        when(jsonTransformer.transform(any())).thenReturn(transformedResult);
        when(transformedResult.map(any())).thenReturn(Optional.of(List.of("data1", "data2").toString()));
        when(fileStore.store(any(), any())).thenReturn(new CloudStoreStateError("Test Error"));
        when(cloudLoadable.retrieve(any(), any())).thenReturn(Optional.of("{}"));

        final CloudStore cloudStorable = new CloudStore(fileStore);

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

        final CloudStore cloudStorable = new CloudStore(fileStore);

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

        try(final LogCapture logData =new LogCapture(HANDLE_RESULT)) {

            handleTransformation.handleRequest(input, context);


            assertThat(logData.getLogs().get(0).getMessage()).contains("Store state OK");
        }
    }

    @Test
    void handleTransformationWithDefaultConNoValidFile(@Mock final Context context){

        final Map<String, String> input = Map.of(
                "Transformation-BucketName", "bucket1",
                "Transformation-KeyName", "key1",
                "Transformed-BucketName", "bucket2",
                "Transformed-KeyName", "key2");


        final HandleTransformation handleTransformation
                = new HandleTransformation();

        final CloudException exception = assertThrows(CloudException.class, () -> handleTransformation.handleRequest(input, context));

        assertThat(exception.getMessage()).contains("Unable to transform file to JSON");
    }

}