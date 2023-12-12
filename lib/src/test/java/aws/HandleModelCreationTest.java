package aws;
import cloud.*;
import com.amazonaws.services.lambda.runtime.Context;
import general.ResultState;
import general.Transformer;
import maker.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import support.LogCapture;

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
    public static final String HANDLE_RESULT = "assistant_configuration_creator.HandleResult";

    @Test
    void errorExpectedModelInputBucketNameNullParameters
            (@Mock final Context context,
             @Mock final CloudStorable cloudStorable,
             @Mock final CloudLoadable<InputStream> cloudLoadable,
             @Mock final CloudCopyable cloudCopyable,
             @Mock final Transformer<InputStream, ResultState<ModelMakerResult, ByteArrayOutputStream>> modelMaker){

        final HandleModelCreation handleModelCreation = new HandleModelCreation(
                modelMaker,
                cloudStorable,
                cloudLoadable,
                cloudCopyable);

        final CloudException cloudException = assertThrows(CloudException.class, () -> handleModelCreation.handleRequest(null, context));
        assertThat(cloudException.getMessage()).contains("Bucket name for Model Input file is missing");
    }

    @Test
    void errorExpectedModelInputBucketNameMissing
            (@Mock final Context context,
             @Mock final CloudStorable cloudStorable,
             @Mock final CloudLoadable<InputStream> cloudLoadable,
             @Mock final CloudCopyable cloudCopyable,
             @Mock final Transformer<InputStream, ResultState<ModelMakerResult, ByteArrayOutputStream>> modelMaker){

        final Map<String, String> input = Map.of(
                "wrong-BucketName", "wrong-KeyName");

        final HandleModelCreation handleModelCreation = new HandleModelCreation(
                modelMaker,
                cloudStorable,
                cloudLoadable,
                cloudCopyable);

        final CloudException cloudException = assertThrows(CloudException.class, () -> handleModelCreation.handleRequest(input, context));
        assertThat(cloudException.getMessage()).contains("Bucket name for Model Input file is missing");
    }

    @Test
    void errorExpectedModelInputBucketNameMissingData
            (@Mock final Context context,
             @Mock final CloudStorable cloudStorable,
             @Mock final CloudLoadable<InputStream> cloudLoadable,
             @Mock final CloudCopyable cloudCopyable,
             @Mock final Transformer<InputStream, ResultState<ModelMakerResult, ByteArrayOutputStream>> modelMaker){

        final Map<String, String> input = Map.of(
                "ModelInput-BucketName", "");

        final HandleModelCreation handleModelCreation = new HandleModelCreation(
                modelMaker,
                cloudStorable,
                cloudLoadable,
                cloudCopyable);

        final CloudException cloudException = assertThrows(CloudException.class, () -> handleModelCreation.handleRequest(input, context));
        assertThat(cloudException.getMessage()).contains("Bucket name for Model Input file is missing");
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

        final CloudException cloudException = assertThrows(CloudException.class, () -> handleModelCreation.handleRequest(input, context));
        assertThat(cloudException.getMessage()).contains("Key name for Model Input file is missing");
    }

    @Test
    void errorUnableToCreateModel
            (@Mock final Context context,
             @Mock final CloudStorable fileStore,
             @Mock final CloudLoadable<InputStream> cloudLoadable,
             @Mock final CloudCopyable cloudCopyable,
             @Mock final Transformer<InputStream, ResultState<ModelMakerResult, ByteArrayOutputStream>> modelMaker){

        final ResultState<ModelMakerResult, ByteArrayOutputStream> modelMakerState
                = new ModelMakerStateError("Error Message");

        when(modelMaker.transform(any())).thenReturn(modelMakerState);
        when(cloudLoadable.retrieve(any() ,any())).thenReturn(Optional.of(new ByteArrayInputStream("test".getBytes())));

        final Map<String, String> input = Map.of(
                "ModelInput-BucketName", "bucket1",
                "ModelInput-KeyName", "key1",
                "Model-BucketName", "bucket2",
                "Model-KeyName", "key2",
                "Assistant-BucketName", "bucket3",
                "Assistant-KeyName","key3");


        final CloudStore cloudStorable = new CloudStore(fileStore);

        final HandleModelCreation handleModelCreation = new HandleModelCreation(
                modelMaker,
                cloudStorable,
                cloudLoadable,
                cloudCopyable);

        final CloudException cloudException = assertThrows(CloudException.class, () -> handleModelCreation.handleRequest(input, context));
        assertThat(cloudException.getMessage()).contains("Error Message");
    }

    @Test
    void errorBucketNameForModelFileIsMissing
            (@Mock final Context context,
             @Mock final CloudStorable cloudStorable,
             @Mock final CloudLoadable<InputStream> cloudLoadable,
             @Mock final CloudCopyable cloudCopyable,
             @Mock final Transformer<InputStream, ResultState<ModelMakerResult, ByteArrayOutputStream>> modelMaker){


        final Map<String, String> input = Map.of(
                "ModelInput-BucketName", "bucket1",
                "ModelInput-KeyName", "key1");

        final HandleModelCreation handleModelCreation = new HandleModelCreation(
                modelMaker,
                cloudStorable,
                cloudLoadable,
                cloudCopyable);

        final CloudException cloudException = assertThrows(CloudException.class, () -> handleModelCreation.handleRequest(input, context));
        assertThat(cloudException.getMessage()).contains("Bucket name for Model file is missing");
    }

    @Test
    void errorKeyNameForModelIsMissing
            (@Mock final Context context,
             @Mock final CloudStorable cloudStorable,
             @Mock final CloudLoadable<InputStream> cloudLoadable,
             @Mock final CloudCopyable cloudCopyable,
             @Mock final Transformer<InputStream, ResultState<ModelMakerResult, ByteArrayOutputStream>> modelMaker){

        final Map<String, String> input = Map.of(
                "ModelInput-BucketName", "bucket1",
                "ModelInput-KeyName", "key1",
                "Model-BucketName", "bucket2");

        final HandleModelCreation handleModelCreation = new HandleModelCreation(
                modelMaker,
                cloudStorable,
                cloudLoadable,
                cloudCopyable);

        final CloudException cloudException = assertThrows(CloudException.class, () -> handleModelCreation.handleRequest(input, context));
        assertThat(cloudException.getMessage()).contains("Key name for Model file is missing");
    }

    @Test
    void errorSavingFile
            (@Mock final Context context,
             @Mock final CloudStorable fileStore,
             @Mock final CloudLoadable<InputStream> cloudLoadable,
             @Mock final CloudCopyable cloudCopyable,
             @Mock final Transformer<InputStream, ResultState<ModelMakerResult, ByteArrayOutputStream>> modelMaker,
             @Mock final ByteArrayOutputStream outputStream,
             @Mock final InputStream inputStream){

        final ResultState<ModelMakerResult, ByteArrayOutputStream> modelMakerState
                = new ModelMakerStateOK( "Model Created", outputStream);

        when(fileStore.store(any(), any())).thenReturn(new CloudStoreStateError("Test Error"));
        when(modelMaker.transform(any())).thenReturn(modelMakerState);
        when(cloudLoadable.retrieve(any(),any())).thenReturn(Optional.ofNullable(inputStream));

        final CloudStore cloudStore = new CloudStore(fileStore);

        final Map<String, String> input = Map.of(
                "ModelInput-BucketName", "bucket1",
                "ModelInput-KeyName", "key1",
                "Model-BucketName", "bucket2",
                "Model-KeyName", "key2",
                "Assistant-BucketName", "bucket3",
                "Assistant-KeyName","key3");

        final HandleModelCreation handleModelCreation = new HandleModelCreation(
                modelMaker,
                cloudStore,
                cloudLoadable,
                cloudCopyable);

        final CloudException cloudException = assertThrows(CloudException.class, () -> handleModelCreation.handleRequest(input, context));
        assertThat(cloudException.getMessage()).contains("Test Error");
    }

    @Test
    void SavingFileWithOutIssue
            (@Mock final Context context,
             @Mock final CloudStorable cloudStorable,
             @Mock final CloudLoadable<InputStream> cloudLoadable,
             @Mock final CloudCopyable cloudCopyable,
             @Mock final Transformer<InputStream, ResultState<ModelMakerResult, ByteArrayOutputStream>> modelMaker,
             @Mock final ByteArrayOutputStream outputStream,
             @Mock final InputStream inputStream){


        final ResultState<ModelMakerResult, ByteArrayOutputStream> modelMakerState
                = new ModelMakerStateOK( "Model Created", outputStream);

        when(cloudStorable.store(any(), any())).thenReturn(new CloudStoreStateOK());
        when(modelMaker.transform(any())).thenReturn(modelMakerState);
        when(cloudLoadable.retrieve(any(),any())).thenReturn(Optional.ofNullable(inputStream));

        final Map<String, String> input = Map.of(
                "ModelInput-BucketName", "bucket1",
                "ModelInput-KeyName", "key1",
                "Model-BucketName", "bucket2",
                "Model-KeyName", "key2",
                "Assistant-BucketName", "bucket1",
                "Assistant-KeyName","key3");

        final HandleModelCreation handleModelCreation = new HandleModelCreation(
                modelMaker,
                cloudStorable,
                cloudLoadable,
                cloudCopyable);

        try(final LogCapture logCapture = new LogCapture(HANDLE_RESULT)) {

            handleModelCreation.handleRequest(input, context);

            assertThat(logCapture.getLogs().get(0).getMessage()).contains("Model Created");
        }

        final ArgumentCaptor<CloudObjectReference> crInput
                = ArgumentCaptor.forClass(CloudObjectReference.class);

        final ArgumentCaptor<CloudObjectReference> crOutput
                = ArgumentCaptor.forClass(CloudObjectReference.class);

        verify(cloudCopyable, times(1)).copy(crInput.capture(), crOutput.capture());

        assertThat(crInput.getValue().getObjectName()).contains("key3");
        assertThat(crOutput.getValue().getObjectName()).contains("key3");

        assertThat(crOutput.getValue().getStoreName()).contains("bucket2");
        assertThat(crInput.getValue().getStoreName()).contains("bucket1");
    }

    @Test
    void handleModelCreationWithDefaultConNoValidFile(@Mock final Context context){

        final Map<String, String> input = Map.of(
                "ModelInput-BucketName", "bucket1",
                "ModelInput-KeyName", "key1",
                "Model-BucketName", "bucket2",
                "Model-KeyName", "key2",
                "Assistant-BucketName", "bucket3",
                "Assistant-KeyName","key3");

        final HandleModelCreation handleModelCreation = new HandleModelCreation();

        final CloudException exception = assertThrows(CloudException.class, () -> handleModelCreation.handleRequest(input, context));

        assertThat(exception.getMessage()).contains("Unable to load model");
    }

}