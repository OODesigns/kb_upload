package com.oodesigns.ai.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.oodesigns.ai.assistant_configuration_creator.HandleResult;
import com.oodesigns.ai.cloud.*;
import com.oodesigns.ai.general.Mappable;
import com.oodesigns.ai.general.Transformer;
import com.oodesigns.ai.json.JSON;
import com.oodesigns.ai.support.LogCapture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@MockitoSettings
class HandleTransformationTest {

    static {
        // Provide a default AWS region for SDK clients
        System.setProperty("aws.region", "us-east-1");
    }

    static Stream<Arguments> missingInputScenarios() {
        return Stream.of(
                Arguments.of(
                        null,
                        "Bucket name for transformation file is missing"
                ),
                Arguments.of(
                        Map.of("wrong Key", "wrong value"),
                        "Bucket name for transformation file is missing"
                ),
                Arguments.of(
                        Map.of("Transformation-BucketName", ""),
                        "Bucket name for transformation file is missing"
                ),
                Arguments.of(
                        Map.of("Transformation-BucketName", "bucket"),
                        "Key name for transformation file is missing"
                )
        );
    }

    @ParameterizedTest(name = "Scenario {index}: expect {1}")
    @MethodSource("missingInputScenarios")
    void missingInputThrows(
            final Map<String, String> input,
            final String expectedMessage,
            @Mock final Context context,
            @Mock final Transformer<JSON, Mappable<List<String>, String, String>> jsonTransformer,
            @Mock final CloudStorable fileStore,
            @Mock final CloudCopyable cloudCopyable,
            @Mock final CloudLoadable<String> cloudLoadable
    ) {
        final HandleTransformation handler = new HandleTransformation(
                cloudLoadable,
                jsonTransformer,
                fileStore,
                cloudCopyable
        );

        final CloudException ex = assertThrows(
                CloudException.class,
                () -> handler.handleRequest(input, context)
        );
        assertThat(ex.getMessage()).contains(expectedMessage);
    }

    @Test
    void errorUnableTransformData(
            @Mock final Context context,
            @Mock final Transformer<JSON, Mappable<List<String>, String, String>> jsonTransformer,
            @Mock final Mappable<List<String>, String, String> transformedResult,
            @Mock final CloudStorable fileStore,
            @Mock final CloudCopyable cloudCopyable,
            @Mock final CloudLoadable<String> cloudLoadable
    ) {
        when(jsonTransformer.transform(any())).thenReturn(transformedResult);
        when(transformedResult.map(any())).thenReturn(Optional.empty());
        when(cloudLoadable.retrieve(any(), any())).thenReturn(Optional.of("{}"));

        final Map<String, String> input = Map.of(
                "Transformation-BucketName", "bucket",
                "Transformation-KeyName", "key",
                "Transformed-BucketName", "bucket",
                "Transformed-KeyName", "key"
        );
        final HandleTransformation handler = new HandleTransformation(
                cloudLoadable,
                jsonTransformer,
                fileStore,
                cloudCopyable
        );

        final CloudException ex = assertThrows(
                CloudException.class,
                () -> handler.handleRequest(input, context)
        );
        assertThat(ex.getMessage()).contains("Unable to transform data");
    }

    @Test
    void errorBucketNameForTransformedFileIsMissing(
            @Mock final Context context,
            @Mock final Transformer<JSON, Mappable<List<String>, String, String>> jsonTransformer,
            @Mock final CloudStorable fileStore,
            @Mock final CloudCopyable cloudCopyable,
            @Mock final CloudLoadable<String> cloudLoadable
    ) {
        final Map<String, String> input = Map.of(
                "Transformation-BucketName", "bucket",
                "Transformation-KeyName", "key"
        );
        final HandleTransformation handler = new HandleTransformation(
                cloudLoadable,
                jsonTransformer,
                fileStore,
                cloudCopyable
        );

        final CloudException ex = assertThrows(
                CloudException.class,
                () -> handler.handleRequest(input, context)
        );
        assertThat(ex.getMessage()).contains("Bucket name for transformed file is missing");
    }

    @Test
    void errorKeyNameForTransformedIsMissing(
            @Mock final Context context,
            @Mock final Transformer<JSON, Mappable<List<String>, String, String>> jsonTransformer,
            @Mock final CloudStorable fileStore,
            @Mock final CloudCopyable cloudCopyable,
            @Mock final CloudLoadable<String> cloudLoadable
    ) {
        final Map<String, String> input = Map.of(
                "Transformation-BucketName", "bucket1",
                "Transformation-KeyName", "key1",
                "Transformed-BucketName", "bucket2"
        );
        final HandleTransformation handler = new HandleTransformation(
                cloudLoadable,
                jsonTransformer,
                fileStore,
                cloudCopyable
        );

        final CloudException ex = assertThrows(
                CloudException.class,
                () -> handler.handleRequest(input, context)
        );
        assertThat(ex.getMessage()).contains("Key name for transformed file is missing");
    }

    @Test
    void errorSavingFile(
            @Mock final Context context,
            @Mock final Transformer<JSON, Mappable<List<String>, String, String>> jsonTransformer,
            @Mock final Mappable<List<String>, String, String> transformedResult,
            @Mock final CloudCopyable cloudCopyable,
            @Mock final CloudLoadable<String> cloudLoadable,
            @Mock final CloudStorable fileStore
    ) {
        when(jsonTransformer.transform(any())).thenReturn(transformedResult);
        when(transformedResult.map(any())).thenReturn(Optional.of(List.of("data1", "data2").toString()));
        when(fileStore.store(any(), any())).thenReturn(new CloudStoreStateError("Test Error"));
        when(cloudLoadable.retrieve(any(), any())).thenReturn(Optional.of("{}"));

        final CloudStore cloudStorable = new CloudStore(fileStore);
        final Map<String, String> input = Map.of(
                "Transformation-BucketName", "bucket1",
                "Transformation-KeyName", "key1",
                "Transformed-BucketName", "bucket2",
                "Transformed-KeyName", "key2"
        );
        final HandleTransformation handler = new HandleTransformation(
                cloudLoadable,
                jsonTransformer,
                cloudStorable,
                cloudCopyable
        );

        final CloudException ex = assertThrows(
                CloudException.class,
                () -> handler.handleRequest(input, context)
        );
        assertThat(ex.getMessage()).contains("Test Error");
    }

    @Test
    void savingFileWithoutIssue(
            @Mock final Context context,
            @Mock final Transformer<JSON, Mappable<List<String>, String, String>> jsonTransformer,
            @Mock final Mappable<List<String>, String, String> transformedResult,
            @Mock final CloudStorable fileStore,
            @Mock final CloudCopyable cloudCopyable,
            @Mock final CloudLoadable<String> cloudLoadable
    ) {
        when(jsonTransformer.transform(any())).thenReturn(transformedResult);
        when(transformedResult.map(any())).thenReturn(Optional.of(List.of("data1", "data2").toString()));
        when(fileStore.store(any(), any())).thenReturn(new CloudStoreStateOK());
        when(cloudLoadable.retrieve(any(), any())).thenReturn(Optional.of("{}"));

        final CloudStore cloudStorable = new CloudStore(fileStore);
        final Map<String, String> input = Map.of(
                "Transformation-BucketName", "bucket1",
                "Transformation-KeyName", "key1",
                "Transformed-BucketName", "bucket2",
                "Transformed-KeyName", "key2"
        );
        final HandleTransformation handler = new HandleTransformation(
                cloudLoadable,
                jsonTransformer,
                cloudStorable,
                cloudCopyable
        );

        try (final LogCapture logData = new LogCapture(HandleResult.class)) {
            handler.handleRequest(input, context);
            assertThat(logData.getLogs().getFirst().getMessage()).contains("Store state OK");
        }
    }

    @Test
    void handleTransformationWithDefaultConNoValidFile(@Mock final Context context) {
        final Map<String, String> input = Map.of(
                "Transformation-BucketName", "bucket1",
                "Transformation-KeyName", "key1",
                "Transformed-BucketName", "bucket2",
                "Transformed-KeyName", "key2"
        );
        final HandleTransformation handler = new HandleTransformation();
        final CloudException ex = assertThrows(
                CloudException.class,
                () -> handler.handleRequest(input, context)
        );
        assertThat(ex.getMessage()).contains("Unable to transform file to JSON");
    }
}
