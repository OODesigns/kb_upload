package com.oodesigns.ai.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.oodesigns.ai.assistant_configuration_creator.HandleResult;
import com.oodesigns.ai.cloud.*;
import com.oodesigns.ai.general.ResultState;
import com.oodesigns.ai.general.Transformer;
import com.oodesigns.ai.maker.ModelMakerResult;
import com.oodesigns.ai.maker.ModelMakerStateError;
import com.oodesigns.ai.maker.ModelMakerStateOK;
import com.oodesigns.ai.support.LogCapture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings
class HandleModelCreationTest {

    static {
        // Ensure AWS SDK has a default region for client initialization
        System.setProperty("aws.region", "us-east-1");
    }

    static Stream<Arguments> missingInputScenarios() {
        return Stream.of(
                Arguments.of(
                        null,
                        "Bucket name for Model Input file is missing"
                ),
                Arguments.of(
                        Map.of("wrong-BucketName", "wrong-KeyName"),
                        "Bucket name for Model Input file is missing"
                ),
                Arguments.of(
                        Map.of("ModelInput-BucketName", ""),
                        "Bucket name for Model Input file is missing"
                ),
                Arguments.of(
                        Map.of("ModelInput-BucketName", "bucket1"),
                        "Key name for Model Input file is missing"
                ),
                Arguments.of(
                        Map.of(
                                "ModelInput-BucketName", "bucket1",
                                "ModelInput-KeyName", "key1"
                        ),
                        "Bucket name for Model file is missing"
                ),
                Arguments.of(
                        Map.of(
                                "ModelInput-BucketName", "bucket1",
                                "ModelInput-KeyName", "key1",
                                "Model-BucketName", "bucket2"
                        ),
                        "Key name for Model file is missing"
                )
        );
    }

    @ParameterizedTest(name = "Scenario {index}: expect {1}")
    @MethodSource("missingInputScenarios")
    void missingInputThrows(
            final Map<String, String> input,
            final String expectedMessage,
            @Mock final Context context,
            @Mock final CloudStorable cloudStorable,
            @Mock final CloudLoadable<InputStream> cloudLoadable,
            @Mock final CloudCopyable cloudCopyable,
            @Mock final Transformer<InputStream, ResultState<ModelMakerResult, ByteArrayOutputStream>> modelMaker
    ) {
        final HandleModelCreation handler =
                new HandleModelCreation(modelMaker,
                        cloudStorable,
                        cloudLoadable,
                        cloudCopyable);

        final CloudException ex = assertThrows(
                CloudException.class,
                () -> handler.handleRequest(input, context)
        );
        assertThat(ex.getMessage()).contains(expectedMessage);
    }

    @Test
    void errorUnableToCreateModel(
            @Mock final Context context,
            @Mock final CloudStorable fileStore,
            @Mock final CloudLoadable<InputStream> cloudLoadable,
            @Mock final CloudCopyable cloudCopyable,
            @Mock final Transformer<InputStream, ResultState<ModelMakerResult, ByteArrayOutputStream>> modelMaker
    ) {
        final ResultState<ModelMakerResult, ByteArrayOutputStream> state =
                new ModelMakerStateError("Error Message");
        when(modelMaker.transform(any())).thenReturn(state);
        when(cloudLoadable.retrieve(any(), any()))
                .thenReturn(Optional.of(new ByteArrayInputStream("test".getBytes())));

        final Map<String, String> input = Map.of(
                "ModelInput-BucketName", "bucket1",
                "ModelInput-KeyName", "key1",
                "Model-BucketName", "bucket2",
                "Model-KeyName", "key2",
                "Assistant-BucketName", "bucket3",
                "Assistant-KeyName", "key3"
        );
        final HandleModelCreation handler = new HandleModelCreation(
                modelMaker,
                new CloudStore(fileStore),
                cloudLoadable,
                cloudCopyable
        );

        final CloudException ex = assertThrows(
                CloudException.class,
                () -> handler.handleRequest(input, context)
        );
        assertThat(ex.getMessage()).contains("Error Message");
    }

    @Test
    void errorSavingFile(
            @Mock final Context context,
            @Mock final CloudStorable fileStore,
            @Mock final CloudLoadable<InputStream> cloudLoadable,
            @Mock final CloudCopyable cloudCopyable,
            @Mock final Transformer<InputStream, ResultState<ModelMakerResult, ByteArrayOutputStream>> modelMaker,
            @Mock final ByteArrayOutputStream outputStream,
            @Mock final InputStream inputStream
    ) {
        final ResultState<ModelMakerResult, ByteArrayOutputStream> state =
                new ModelMakerStateOK("Model Created", outputStream);
        when(fileStore.store(any(), any()))
                .thenReturn(new CloudStoreStateError("Test Error"));
        when(modelMaker.transform(any())).thenReturn(state);
        when(cloudLoadable.retrieve(any(), any()))
                .thenReturn(Optional.ofNullable(inputStream));

        final CloudStore storable = new CloudStore(fileStore);
        final Map<String, String> input = Map.of(
                "ModelInput-BucketName", "bucket1",
                "ModelInput-KeyName", "key1",
                "Model-BucketName", "bucket2",
                "Model-KeyName", "key2",
                "Assistant-BucketName", "bucket3",
                "Assistant-KeyName", "key3"
        );
        final HandleModelCreation handler =
                new HandleModelCreation(modelMaker,
                        storable,
                        cloudLoadable,
                        cloudCopyable);

        final CloudException ex = assertThrows(
                CloudException.class,
                () -> handler.handleRequest(input, context)
        );
        assertThat(ex.getMessage()).contains("Test Error");
    }

    @Test
    void SavingFileWithOutIssue(
            @Mock final Context context,
            @Mock final CloudStorable cloudStorable,
            @Mock final CloudLoadable<InputStream> cloudLoadable,
            @Mock final CloudCopyable cloudCopyable,
            @Mock final Transformer<InputStream, ResultState<ModelMakerResult, ByteArrayOutputStream>> modelMaker,
            @Mock final ByteArrayOutputStream outputStream,
            @Mock final InputStream inputStream
    ) {
        final ResultState<ModelMakerResult, ByteArrayOutputStream> state =
                new ModelMakerStateOK("Model Created", outputStream);
        when(cloudStorable.store(any(), any()))
                .thenReturn(new CloudStoreStateOK());
        when(modelMaker.transform(any())).thenReturn(state);
        when(cloudLoadable.retrieve(any(), any()))
                .thenReturn(Optional.ofNullable(inputStream));

        final Map<String, String> input = Map.of(
                "ModelInput-BucketName", "bucket1",
                "ModelInput-KeyName", "key1",
                "Model-BucketName", "bucket2",
                "Model-KeyName", "key2",
                "Assistant-BucketName", "bucket1",
                "Assistant-KeyName", "key3"
        );
        final HandleModelCreation handler =
                new HandleModelCreation(modelMaker,
                        cloudStorable,
                        cloudLoadable,
                        cloudCopyable);
        try (final LogCapture logCapture = new LogCapture(HandleResult.class)) {
            handler.handleRequest(input, context);
            assertThat(logCapture.getLogs().getFirst().getMessage())
                    .contains("Model Created");
        }

        final ArgumentCaptor<CloudObjectReference> crInput =
                ArgumentCaptor.forClass(CloudObjectReference.class);
        final ArgumentCaptor<CloudObjectReference> crOutput =
                ArgumentCaptor.forClass(CloudObjectReference.class);
        verify(cloudCopyable, times(1))
                .copy(crInput.capture(), crOutput.capture());
        assertThat(crInput.getValue().getObjectName())
                .contains("key3");
        assertThat(crOutput.getValue().getObjectName())
                .contains("key3");
        assertThat(crOutput.getValue().getStoreName())
                .contains("bucket2");
        assertThat(crInput.getValue().getStoreName())
                .contains("bucket1");
    }

    @Test
    void handleModelCreationWithDefaultConNoValidFile(
            @Mock final Context context
    ) {
        final Map<String, String> input = Map.of(
                "ModelInput-BucketName", "bucket1",
                "ModelInput-KeyName", "key1",
                "Model-BucketName", "bucket2",
                "Model-KeyName", "key2",
                "Assistant-BucketName", "bucket3",
                "Assistant-KeyName", "key3"
        );
        final HandleModelCreation handler = new HandleModelCreation();
        final CloudException exception =
                assertThrows(CloudException.class,
                        () -> handler.handleRequest(input, context));
        assertThat(exception.getMessage())
                .contains("Unable to load model");
    }
}
