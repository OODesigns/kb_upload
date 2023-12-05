package aws;
import cloud.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import general.ResultState;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings
class HandleModelCreationTest {

    @Test
    void errorExpectedModelInputBucketNameNullParameters
            (@Mock final Context context,
             @Mock final CloudStorable cloudStorable,
             @Mock final CloudLoadable<InputStream> cloudLoadable,
             @Mock final CloudCopyable cloudCopyable,
             @Mock final Transformer<InputStream, ResultState<ModelMakerResult, ByteArrayOutputStream>> modelMaker){

        when(cloudLoadable.retrieve(any(), any())).thenReturn(Optional.of(new ByteArrayInputStream("{}".getBytes())));

        final HandleModelCreation handleModelCreation = new HandleModelCreation(
                modelMaker,
                cloudStorable,
                cloudLoadable,
                cloudCopyable);

        assertThrows(CloudException.class, ()-> handleModelCreation.handleRequest(null, context));
    }

    @Test
    void errorExpectedModelInputBucketNameMissing
            (@Mock final Context context,
             @Mock final CloudStorable cloudStorable,
             @Mock final CloudLoadable<InputStream> cloudLoadable,
             @Mock final CloudCopyable cloudCopyable,
             @Mock final Transformer<InputStream, ResultState<ModelMakerResult, ByteArrayOutputStream>> modelMaker){


        when(cloudLoadable.retrieve(any(), any())).thenReturn(Optional.of(new ByteArrayInputStream("{}".getBytes())));

        final Map<String, String> input = Map.of(
                "wrong-BucketName", "wrong-KeyName");

        final HandleModelCreation handleModelCreation = new HandleModelCreation(
                modelMaker,
                cloudStorable,
                cloudLoadable,
                cloudCopyable);

        assertThrows(CloudException.class, ()-> handleModelCreation.handleRequest(input, context));
    }

    @Test
    void errorExpectedModelInputBucketNameMissingData
            (@Mock final Context context,
             @Mock final CloudStorable cloudStorable,
             @Mock final CloudLoadable<InputStream> cloudLoadable,
             @Mock final CloudCopyable cloudCopyable,
             @Mock final Transformer<InputStream, ResultState<ModelMakerResult, ByteArrayOutputStream>> modelMaker){

        when(cloudLoadable.retrieve(any(), any())).thenReturn(Optional.of(new ByteArrayInputStream("{}".getBytes())));

        final Map<String, String> input = Map.of(
                "ModelInput-BucketName", "");

        final HandleModelCreation handleModelCreation = new HandleModelCreation(
                modelMaker,
                cloudStorable,
                cloudLoadable,
                cloudCopyable);

        assertThrows(CloudException.class, ()-> handleModelCreation.handleRequest(input, context));
    }

    @Test
    void errorExpectedModelInputKeyNameMissing
            (@Mock final Context context,
             @Mock final CloudStorable cloudStorable,
             @Mock final CloudLoadable<InputStream> cloudLoadable,
             @Mock final CloudCopyable cloudCopyable,
             @Mock final Transformer<InputStream, ResultState<ModelMakerResult, ByteArrayOutputStream>> modelMaker){

        final Map<String, String> input = Map.of(
                "ModelInput-BucketName", "bucket1");

        final HandleModelCreation handleModelCreation = new HandleModelCreation(
                modelMaker,
                cloudStorable,
                cloudLoadable,
                cloudCopyable);

        assertThrows(CloudException.class, ()-> handleModelCreation.handleRequest(input, context));
    }

    @Test
    void errorUnableToCreateModel
            (@Mock final Context context,
             @Mock final CloudStorable cloudStorable,
             @Mock final CloudLoadable<InputStream> cloudLoadable,
             @Mock final CloudCopyable cloudCopyable,
             @Mock final Transformer<InputStream, ResultState<ModelMakerResult, ByteArrayOutputStream>> modelMaker){

        final ResultState<ModelMakerResult, ByteArrayOutputStream> modelMakerState
                = new ModelMakerStateError("Error Message");

        when(modelMaker.transform(any())).thenReturn(modelMakerState);

        final Map<String, String> input = Map.of(
                "ModelInput-BucketName", "bucket1",
                "ModelInput-KeyName", "key1");

        final HandleModelCreation handleModelCreation = new HandleModelCreation(
                modelMaker,
                cloudStorable,
                cloudLoadable,
                cloudCopyable);

        assertThrows(CloudException.class, ()-> handleModelCreation.handleRequest(input, context));
    }

    @Test
    void errorBucketNameForModelFileIsMissing
            (@Mock final Context context,
             @Mock final CloudStorable cloudStorable,
             @Mock final CloudLoadable<InputStream> cloudLoadable,
             @Mock final CloudCopyable cloudCopyable,
             @Mock final Transformer<InputStream, ResultState<ModelMakerResult, ByteArrayOutputStream>> modelMaker,
             @Mock final ByteArrayOutputStream outputStream){


        final ResultState<ModelMakerResult, ByteArrayOutputStream> modelMakerState
                = new ModelMakerStateOK( "Model Created", outputStream);

        when(modelMaker.transform(any())).thenReturn(modelMakerState);

        final Map<String, String> input = Map.of(
                "ModelInput-BucketName", "bucket1",
                "ModelInput-KeyName", "key1");

        final HandleModelCreation handleModelCreation = new HandleModelCreation(
                modelMaker,
                cloudStorable,
                cloudLoadable,
                cloudCopyable);

        assertThrows(CloudException.class, ()-> handleModelCreation.handleRequest(input, context));
    }

    @Test
    void errorKeyNameForModelIsMissing
            (@Mock final Context context,
             @Mock final CloudStorable cloudStorable,
             @Mock final CloudLoadable<InputStream> cloudLoadable,
             @Mock final CloudCopyable cloudCopyable,
             @Mock final Transformer<InputStream, ResultState<ModelMakerResult, ByteArrayOutputStream>> modelMaker,
             @Mock final ByteArrayOutputStream outputStream){

        final ResultState<ModelMakerResult, ByteArrayOutputStream> modelMakerState
                = new ModelMakerStateOK( "Model Created", outputStream);

        when(modelMaker.transform(any())).thenReturn(modelMakerState);

        final Map<String, String> input = Map.of(
                "ModelInput-BucketName", "bucket1",
                "ModelInput-KeyName", "key1",
                "Model-BucketName", "bucket2");

        final HandleModelCreation handleModelCreation = new HandleModelCreation(
                modelMaker,
                cloudStorable,
                cloudLoadable,
                cloudCopyable);

        assertThrows(CloudException.class, ()-> handleModelCreation.handleRequest(input, context));
    }

    @Test
    void errorSavingFile
            (@Mock final Context context,
             @Mock final CloudStorable cloudStorable,
             @Mock final CloudLoadable<InputStream> cloudLoadable,
             @Mock final CloudCopyable cloudCopyable,
             @Mock final Transformer<InputStream, ResultState<ModelMakerResult, ByteArrayOutputStream>> modelMaker,
             @Mock final ByteArrayOutputStream outputStream){

        final ResultState<ModelMakerResult, ByteArrayOutputStream> modelMakerState
                = new ModelMakerStateOK( "Model Created", outputStream);

        when(cloudStorable.store(any(), any())).thenReturn(new CloudStoreStateError("Test Error"));
        when(modelMaker.transform(any())).thenReturn(modelMakerState);

        final Map<String, String> input = Map.of(
                "ModelInput-BucketName", "bucket1",
                "ModelInput-KeyName", "key1",
                "Model-BucketName", "bucket2",
                "Model-KeyName", "key2");

        final HandleModelCreation handleModelCreation = new HandleModelCreation(
                modelMaker,
                cloudStorable,
                cloudLoadable,
                cloudCopyable);

        assertThrows(CloudException.class, ()-> handleModelCreation.handleRequest(input, context));

        final ArgumentCaptor<ByteArrayOutputStream> data = ArgumentCaptor.forClass(ByteArrayOutputStream.class);
        verify(cloudStorable, times(1)).store(any(), data.capture());
    }

    @Test
    void SavingFileWithOutIssue
            (@Mock final Context context,
             @Mock final CloudStorable cloudStorable,
             @Mock final CloudLoadable<InputStream> cloudLoadable,
             @Mock final CloudCopyable cloudCopyable,
             @Mock final Transformer<InputStream, ResultState<ModelMakerResult, ByteArrayOutputStream>> modelMaker,
             @Mock final ByteArrayOutputStream outputStream){


        final ResultState<ModelMakerResult, ByteArrayOutputStream> modelMakerState
                = new ModelMakerStateOK( "Model Created", outputStream);

        when(cloudStorable.store(any(), any())).thenReturn(new CloudStoreStateOK());
        when(modelMaker.transform(any())).thenReturn(modelMakerState);

        final Map<String, String> input = Map.of(
                "ModelInput-BucketName", "bucket1",
                "ModelInput-KeyName", "key1",
                "Model-BucketName", "bucket2",
                "Model-KeyName", "key2");

        final HandleModelCreation handleModelCreation = new HandleModelCreation(
                modelMaker,
                cloudStorable,
                cloudLoadable,
                cloudCopyable);

        handleModelCreation.handleRequest(input, context);

        final ArgumentCaptor<ByteArrayOutputStream> data = ArgumentCaptor.forClass(ByteArrayOutputStream.class);
        verify(cloudStorable, times(1)).store(any(), data.capture());
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

        final HandleModelCreation handleModelCreation = new HandleModelCreation();

        assertThrows(CloudException.class, ()-> handleModelCreation.handleRequest(input, context));
    }

}