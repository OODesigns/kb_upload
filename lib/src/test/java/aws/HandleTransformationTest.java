package aws;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import kb_upload.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import com.amazonaws.services.lambda.runtime.Context;
import software.amazon.awssdk.regions.Region;

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
    void errorExpectedUploadBucketNameNullParameters(@Mock final Context context,
                                       @Mock final LambdaLogger lambdaLogger,
                                       @Mock final Retrievable<S3Object, Optional<String>> fileLoader,
                                       @Mock final Transformer<JSON, mappable<List<String>, String, String>> jsonTransformer,
                                       @Mock final Retrievable<Region, Storable<S3Object, String, S3FileSaverState>> fileStoreProvider){

        when(context.getLogger()).thenReturn(lambdaLogger);

        final HandleTransformation handleTransformation
                = new HandleTransformation(fileLoader, jsonTransformer, fileStoreProvider);

        assertThrows(TransformationException.class, ()->handleTransformation.handleRequest(null, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("Bucket name for transformation is missing");
    }

    @Test
    void errorExpectedUploadBucketNameMissing(@Mock final Context context,
                                              @Mock final LambdaLogger lambdaLogger,
                                              @Mock final Retrievable<S3Object, Optional<String>> fileLoader,
                                              @Mock final Transformer<JSON, mappable<List<String>, String, String>> jsonTransformer,
                                              @Mock final Retrievable<Region, Storable<S3Object, String, S3FileSaverState>> fileStoreProvider){

        when(context.getLogger()).thenReturn(lambdaLogger);

        final Map<String, String> input = Map.of("wrong Key", "wrong value");

        final HandleTransformation handleTransformation
                = new HandleTransformation(fileLoader, jsonTransformer, fileStoreProvider);

        assertThrows(TransformationException.class, ()->handleTransformation.handleRequest(input, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("Bucket name for transformation is missing");
    }

    @Test
    void errorExpectedUploadBucketNameDataMissing(@Mock final Context context,
                                              @Mock final LambdaLogger lambdaLogger,
                                              @Mock final Retrievable<S3Object, Optional<String>> fileLoader,
                                              @Mock final Transformer<JSON, mappable<List<String>, String, String>> jsonTransformer,
                                              @Mock final Retrievable<Region, Storable<S3Object, String, S3FileSaverState>> fileStoreProvider){

        when(context.getLogger()).thenReturn(lambdaLogger);

        final Map<String, String> input = Map.of("Transformation-BucketName", "");

        final HandleTransformation handleTransformation
                = new HandleTransformation(fileLoader, jsonTransformer, fileStoreProvider);

        assertThrows(TransformationException.class, ()->handleTransformation.handleRequest(input, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("Bucket name for transformation is missing");
    }

    @Test
    void errorUnableToLoadFile(@Mock final Context context,
                               @Mock final LambdaLogger lambdaLogger,
                               @Mock final Retrievable<S3Object, Optional<String>> fileLoader,
                               @Mock final Transformer<JSON, mappable<List<String>, String, String>> jsonTransformer,
                               @Mock final Retrievable<Region, Storable<S3Object, String, S3FileSaverState>> fileStoreProvider){

        when(context.getLogger()).thenReturn(lambdaLogger);
        when(fileLoader.retrieve(any())).thenReturn(Optional.empty());

        final Map<String, String> input = Map.of("Transformation-BucketName", "bucket");

        final HandleTransformation handleTransformation
                = new HandleTransformation(fileLoader, jsonTransformer, fileStoreProvider);

        assertThrows(TransformationException.class, ()->handleTransformation.handleRequest(input, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("Unable to load file from bucket: bucket and key: knowledge.json");
    }

    @Test
    void errorUnableTransformData(@Mock final Context context,
                               @Mock final LambdaLogger lambdaLogger,
                               @Mock final Retrievable<S3Object, Optional<String>> fileLoader,
                               @Mock final Transformer<JSON, mappable<List<String>, String, String>> jsonTransformer,
                               @Mock final mappable<List<String>, String, String>  transformedResult,
                               @Mock final Retrievable<Region, Storable<S3Object, String, S3FileSaverState>> fileStoreProvider){

        when(context.getLogger()).thenReturn(lambdaLogger);
        when(fileLoader.retrieve(any(S3Object.class))).thenReturn(Optional.of("{\"nothing\":\"some text\"}"));

        when(jsonTransformer.transform(any())).thenReturn(transformedResult);
        when(transformedResult.map(any())).thenReturn(Optional.empty());

        final Map<String, String> input = Map.of("Transformation-BucketName", "bucket");

        final HandleTransformation handleTransformation
                = new HandleTransformation(fileLoader, jsonTransformer, fileStoreProvider);

        assertThrows(TransformationException.class, ()->handleTransformation.handleRequest(input, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("Unable to transform data");
    }

    @Test
    void errorBucketNameForTransFormedFileIsMissing
                                  (@Mock final Context context,
                                   @Mock final LambdaLogger lambdaLogger,
                                   @Mock final Retrievable<S3Object, Optional<String>> fileLoader,
                                   @Mock final Transformer<JSON, mappable<List<String>, String, String>> jsonTransformer,
                                   @Mock final mappable<List<String>, String, String>  transformedResult,
                                   @Mock final Retrievable<Region, Storable<S3Object, String, S3FileSaverState>> fileStoreProvider){

        when(context.getLogger()).thenReturn(lambdaLogger);
        when(fileLoader.retrieve(any(S3Object.class))).thenReturn(Optional.of("{\"nothing\":\"some text\"}"));
        when(jsonTransformer.transform(any())).thenReturn(transformedResult);
        when(transformedResult.map(any())).thenReturn(Optional.of(List.of("data1", "data2").toString()));

        final Map<String, String> input = Map.of("Transformation-BucketName", "bucket");

        final HandleTransformation handleTransformation
                = new HandleTransformation(fileLoader, jsonTransformer, fileStoreProvider);

        assertThrows(TransformationException.class, ()->handleTransformation.handleRequest(input, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("Bucket name for transformed file is missing");
    }

    @Test
    void errorRegionForBucketMissing
            (@Mock final Context context,
             @Mock final LambdaLogger lambdaLogger,
             @Mock final Retrievable<S3Object, Optional<String>> fileLoader,
             @Mock final Transformer<JSON, mappable<List<String>, String, String>> jsonTransformer,
             @Mock final mappable<List<String>, String, String>  transformedResult,
             @Mock final Retrievable<Region, Storable<S3Object, String, S3FileSaverState>> fileStoreProvider){

        when(context.getLogger()).thenReturn(lambdaLogger);
        when(fileLoader.retrieve(any(S3Object.class))).thenReturn(Optional.of("{\"nothing\":\"some text\"}"));
        when(jsonTransformer.transform(any())).thenReturn(transformedResult);
        when(transformedResult.map(any())).thenReturn(Optional.of(List.of("data1", "data2").toString()));

        final Map<String, String> input = Map.of("Transformation-BucketName", "bucket1",
                                                 "Transformed-BucketName", "bucket2");

        final HandleTransformation handleTransformation
                = new HandleTransformation(fileLoader, jsonTransformer, fileStoreProvider);

        assertThrows(TransformationException.class, ()->handleTransformation.handleRequest(input, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("Region name for transformed file is missing or invalid");
    }

    @Test
    void errorSavingFile
            (@Mock final Context context,
             @Mock final LambdaLogger lambdaLogger,
             @Mock final Retrievable<S3Object, Optional<String>> fileLoader,
             @Mock final Transformer<JSON, mappable<List<String>, String, String>> jsonTransformer,
             @Mock final mappable<List<String>, String, String>  transformedResult,
             @Mock final Retrievable<Region, Storable<S3Object, String, S3FileSaverState>> fileStoreProvider,
             @Mock final Storable<S3Object, String, S3FileSaverState> storable){

        when(context.getLogger()).thenReturn(lambdaLogger);
        when(fileLoader.retrieve(any(S3Object.class))).thenReturn(Optional.of("{\"nothing\":\"some text\"}"));
        when(jsonTransformer.transform(any())).thenReturn(transformedResult);
        when(transformedResult.map(any())).thenReturn(Optional.of(List.of("data1", "data2").toString()));
        when(fileStoreProvider.retrieve(any())).thenReturn(storable);
        when(storable.store(any(), any())).thenReturn(new S3FileSaverErrorState("Test Error"));


        final Map<String, String> input = Map.of("Transformation-BucketName", "bucket1",
                                                 "Transformed-BucketName", "bucket2",
                                                 "Transformed-Region", "eu-north-1");

        final HandleTransformation handleTransformation
                = new HandleTransformation(fileLoader, jsonTransformer, fileStoreProvider);

        assertThrows(TransformationException.class, ()->handleTransformation.handleRequest(input, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("Test Error");
    }

    @Test
    void SavingFileWithOutIssue
            (@Mock final Context context,
             @Mock final LambdaLogger lambdaLogger,
             @Mock final Retrievable<S3Object, Optional<String>> fileLoader,
             @Mock final Transformer<JSON, mappable<List<String>, String, String>> jsonTransformer,
             @Mock final mappable<List<String>, String, String>  transformedResult,
             @Mock final Retrievable<Region, Storable<S3Object, String, S3FileSaverState>> fileStoreProvider,
             @Mock final Storable<S3Object, String, S3FileSaverState> storable){

        when(context.getLogger()).thenReturn(lambdaLogger);
        when(fileLoader.retrieve(any(S3Object.class))).thenReturn(Optional.of("{\"nothing\":\"some text\"}"));
        when(jsonTransformer.transform(any())).thenReturn(transformedResult);
        when(transformedResult.map(any())).thenReturn(Optional.of(List.of("data1", "data2").toString()));
        when(fileStoreProvider.retrieve(any())).thenReturn(storable);
        when(storable.store(any(), any())).thenReturn(new S3FileSaverOKState());


        final Map<String, String> input = Map.of("Transformation-BucketName", "bucket1",
                "Transformed-BucketName", "bucket2",
                "Transformed-Region", "eu-north-1");

        final HandleTransformation handleTransformation
                = new HandleTransformation(fileLoader, jsonTransformer, fileStoreProvider);

        handleTransformation.handleRequest(input, context);

        final ArgumentCaptor<String> data = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);

        verify(storable, times(1)).store(any(), data.capture());
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(data.getValue()).contains("data1", "data2");
        assertThat(logData.getValue()).contains("S3FileSaverOKState");
    }

    @Test
    void handleTransformationWithDefaultConNoValidFile(@Mock final Context context,
                                                       @Mock final LambdaLogger lambdaLogger){

        final Map<String, String> input = Map.of("Transformation-BucketName", "bucket1",
                    "Transformed-BucketName", "bucket2",
                    "Transformed-Region", "eu-north-1");

        when(context.getLogger()).thenReturn(lambdaLogger);


        final HandleTransformation handleTransformation
                = new HandleTransformation();

        assertThrows(TransformationException.class, ()-> handleTransformation.handleRequest(input, context));
    }





}