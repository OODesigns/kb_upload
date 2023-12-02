package aws;

import aws.root.*;
import cloud.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import general.Transformer;
import maker.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings
class HandleModelCreationTest {

    @Test
    void errorExpectedModelInputBucketNameNullParameters
            (@Mock final Context context,
             @Mock final LambdaLogger lambdaLogger,
             @Mock final CloudStorable fileStore,
             @Mock final Transformer<InputStream, ModelMakerState<ModelMakerResult>> modelMaker){

        when(context.getLogger()).thenReturn(lambdaLogger);

        final HandleModelCreation handleModelCreation
                = new HandleModelCreation(
                __-> Optional.of(new ByteArrayInputStream("{}".getBytes())),
                modelMaker,
                fileStore);

        assertThrows(AWSS3Exception.class, ()->handleModelCreation.handleRequest(null, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("Bucket name for Model Input file is missing");
    }

    @Test
    void errorExpectedModelInputBucketNameMissing
            (@Mock final Context context,
             @Mock final LambdaLogger lambdaLogger,
             @Mock final CloudStorable fileStore,
             @Mock final Transformer<InputStream, ModelMakerState<ModelMakerResult>> modelMaker){

        when(context.getLogger()).thenReturn(lambdaLogger);

        final Map<String, String> input = Map.of(
                "wrong-BucketName", "wrong-KeyName");

        final HandleModelCreation handleModelCreation
                = new HandleModelCreation(
                __-> Optional.of(new ByteArrayInputStream("{}".getBytes())),
                modelMaker,
                fileStore);

        assertThrows(AWSS3Exception.class, ()->handleModelCreation.handleRequest(input, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("Bucket name for Model Input file is missing");
    }

    @Test
    void errorExpectedModelInputBucketNameMissingData
            (@Mock final Context context,
             @Mock final LambdaLogger lambdaLogger,
             @Mock final CloudStorable fileStore,
             @Mock final Transformer<InputStream, ModelMakerState<ModelMakerResult>> modelMaker){

        when(context.getLogger()).thenReturn(lambdaLogger);

        final Map<String, String> input = Map.of(
                "ModelInput-BucketName", "");

        final HandleModelCreation handleModelCreation
                = new HandleModelCreation(
                __-> Optional.of(new ByteArrayInputStream("{}".getBytes())),
                modelMaker,
                fileStore);

        assertThrows(AWSS3Exception.class, ()->handleModelCreation.handleRequest(input, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("Bucket name for Model Input file is missing");
    }

    @Test
    void errorExpectedModelInputKeyNameMissing
            (@Mock final Context context,
             @Mock final LambdaLogger lambdaLogger,
             @Mock final CloudStorable fileStore,
             @Mock final Transformer<InputStream, ModelMakerState<ModelMakerResult>> modelMaker){

        when(context.getLogger()).thenReturn(lambdaLogger);

        final Map<String, String> input = Map.of(
                "ModelInput-BucketName", "bucket1");

        final HandleModelCreation handleModelCreation
                = new HandleModelCreation(
                __-> Optional.of(new ByteArrayInputStream("{}".getBytes())),
                modelMaker,
                fileStore);

        assertThrows(AWSS3Exception.class, ()->handleModelCreation.handleRequest(input, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("Key name for Model Input file is missing");
    }

    @Test
    void errorUnableToCreateModel
            (@Mock final Context context,
             @Mock final LambdaLogger lambdaLogger,
             @Mock final CloudStorable fileStore,
             @Mock final Transformer<InputStream, ModelMakerState<ModelMakerResult>> modelMaker){

        final ModelMakerState<ModelMakerResult> modelMakerState
                = new ModelMakerStateError("Error Message");

        when(context.getLogger()).thenReturn(lambdaLogger);
        when(modelMaker.transform(any())).thenReturn(modelMakerState);

        final Map<String, String> input = Map.of(
                "ModelInput-BucketName", "bucket1",
                "ModelInput-KeyName", "key1");

        final HandleModelCreation handleModelCreation
                = new HandleModelCreation(
                __-> Optional.of(new ByteArrayInputStream("{}".getBytes())),
                modelMaker,
                fileStore);

        assertThrows(AWSS3Exception.class, ()->handleModelCreation.handleRequest(input, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(1)).log(logData.capture());

        assertThat(logData.getValue()).contains("Unable To create a model");
    }

    @Test
    void errorBucketNameForModelFileIsMissing
            (@Mock final Context context,
             @Mock final LambdaLogger lambdaLogger,
             @Mock final CloudStorable fileStore,
             @Mock final Transformer<InputStream, ModelMakerState<ModelMakerResult>> modelMaker,
             @Mock final ByteArrayOutputStream outputStream){

        when(context.getLogger()).thenReturn(lambdaLogger);

        final ModelMakerState<ModelMakerResult> modelMakerState
                = new ModelMakerStateOK( "Model Created", outputStream);

        when(modelMaker.transform(any())).thenReturn(modelMakerState);

        final Map<String, String> input = Map.of(
                "ModelInput-BucketName", "bucket1",
                "ModelInput-KeyName", "key1");

        final HandleModelCreation handleModelCreation
                = new HandleModelCreation(
                __-> Optional.of(new ByteArrayInputStream("{}".getBytes())),
                modelMaker,
                fileStore);

        assertThrows(AWSS3Exception.class, ()->handleModelCreation.handleRequest(input, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(2)).log(logData.capture());

        assertThat(logData.getValue()).contains("Bucket name for Model file is missing");
    }
    @Test
    void errorKeyNameForModelIsMissing
            (@Mock final Context context,
             @Mock final LambdaLogger lambdaLogger,
             @Mock final CloudStorable fileStore,
             @Mock final Transformer<InputStream, ModelMakerState<ModelMakerResult>> modelMaker,
             @Mock final ByteArrayOutputStream outputStream){

        final ModelMakerState<ModelMakerResult> modelMakerState
                = new ModelMakerStateOK( "Model Created", outputStream);

        when(context.getLogger()).thenReturn(lambdaLogger);
        when(modelMaker.transform(any())).thenReturn(modelMakerState);

        final Map<String, String> input = Map.of(
                "ModelInput-BucketName", "bucket1",
                "ModelInput-KeyName", "key1",
                "Model-BucketName", "bucket2");

        final HandleModelCreation handleModelCreation
                = new HandleModelCreation(
                __-> Optional.of(new ByteArrayInputStream("{}".getBytes())),
                modelMaker,
                fileStore);

        assertThrows(AWSS3Exception.class, ()->handleModelCreation.handleRequest(input, context));

        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);
        verify(lambdaLogger, times(2)).log(logData.capture());

        assertThat(logData.getValue()).contains("Key name for Model file is missing");
    }

    @Test
    void errorSavingFile
            (@Mock final Context context,
             @Mock final LambdaLogger lambdaLogger,
             @Mock final CloudStorable fileStore,
             @Mock final Transformer<InputStream, ModelMakerState<ModelMakerResult>> modelMaker,
             @Mock final ByteArrayOutputStream outputStream){

        final ModelMakerState<ModelMakerResult> modelMakerState
                = new ModelMakerStateOK( "Model Created", outputStream);

        when(context.getLogger()).thenReturn(lambdaLogger);
        when(fileStore.store(any(), any())).thenReturn(new CloudSaverStateError("Test Error"));
        when(modelMaker.transform(any())).thenReturn(modelMakerState);

        final Map<String, String> input = Map.of(
                "ModelInput-BucketName", "bucket1",
                "ModelInput-KeyName", "key1",
                "Model-BucketName", "bucket2",
                "Model-KeyName", "key2");

        final HandleModelCreation handleModelCreation
                = new HandleModelCreation(
                __-> Optional.of(new ByteArrayInputStream("{}".getBytes())),
                modelMaker,
                fileStore);

        assertThrows(AWSS3Exception.class, ()->handleModelCreation.handleRequest(input, context));

        final ArgumentCaptor<ByteArrayOutputStream> data = ArgumentCaptor.forClass(ByteArrayOutputStream.class);
        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);

        verify(fileStore, times(1)).store(any(), data.capture());
        verify(lambdaLogger, times(2)).log(logData.capture());

        assertThat(logData.getValue()).contains("Test Error");
    }
    @Test
    void SavingFileWithOutIssue
            (@Mock final Context context,
             @Mock final LambdaLogger lambdaLogger,
             @Mock final CloudStorable fileStore,
             @Mock final Transformer<InputStream, ModelMakerState<ModelMakerResult>> modelMaker,
             @Mock final ByteArrayOutputStream outputStream){


        final ModelMakerState<ModelMakerResult> modelMakerState
                = new ModelMakerStateOK( "Model Created", outputStream);

        when(context.getLogger()).thenReturn(lambdaLogger);
        when(fileStore.store(any(), any())).thenReturn(new CloudSaverStateOK());
        when(modelMaker.transform(any())).thenReturn(modelMakerState);

        final Map<String, String> input = Map.of(
                "ModelInput-BucketName", "bucket1",
                "ModelInput-KeyName", "key1",
                "Model-BucketName", "bucket2",
                "Model-KeyName", "key2");

        final HandleModelCreation handleModelCreation
                = new HandleModelCreation(
                __-> Optional.of(new ByteArrayInputStream("{}".getBytes())),
                modelMaker,
                fileStore);

        handleModelCreation.handleRequest(input, context);

        final ArgumentCaptor<ByteArrayOutputStream> data = ArgumentCaptor.forClass(ByteArrayOutputStream.class);
        final ArgumentCaptor<String> logData = ArgumentCaptor.forClass(String.class);

        verify(fileStore, times(1)).store(any(), data.capture());
        verify(lambdaLogger, times(2)).log(logData.capture());

        assertThat(logData.getValue()).contains("Model Created");
    }

    @Test
    void handleModelCreationWithDefaultConNoValidFile(@Mock final Context context,
                                                      @Mock final LambdaLogger lambdaLogger){

        final Map<String, String> input = Map.of(
                "ModelInput-BucketName", "bucket1",
                "ModelInput-KeyName", "key1",
                "Model-BucketName", "bucket2",
                "Model-KeyName", "key2");

        when(context.getLogger()).thenReturn(lambdaLogger);


        final HandleModelCreation handleModelCreation
                = new HandleModelCreation();

        assertThrows(AWSS3Exception.class, ()-> handleModelCreation.handleRequest(input, context));
    }

}