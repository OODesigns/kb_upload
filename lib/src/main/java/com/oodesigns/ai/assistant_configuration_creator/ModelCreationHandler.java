package com.oodesigns.ai.assistant_configuration_creator;
import com.oodesigns.ai.cloud.*;
import com.oodesigns.ai.general.ResultState;
import com.oodesigns.ai.general.Transformer;
import com.oodesigns.ai.maker.ModelMakerResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Function;


public class ModelCreationHandler {
    public static final String UNABLE_TO_CREATE_A_MODEL = "Unable To create a model: %s";
    private final CloudLoadable<InputStream> cloudLoadable;
    private final CloudStorable cloudStorable;
    private final Transformer<InputStream, ResultState<ModelMakerResult, ByteArrayOutputStream>> modelMaker;
    private final CloudCopyable cloudCopyable;

    public ModelCreationHandler(final Transformer<InputStream, ResultState<ModelMakerResult, ByteArrayOutputStream>> modelMaker,
                                final CloudStorable cloudStorable,
                                final CloudLoadable<InputStream> cloudLoadable,
                                final CloudCopyable cloudCopyable) {
        this.cloudLoadable = cloudLoadable;
        this.cloudStorable = cloudStorable;
        this.modelMaker = modelMaker;
        this.cloudCopyable = cloudCopyable;
    }

    public void handleRequest(final CloudObjectReference input,
                              final CloudObjectReference output,
                              final CloudObjectReference assistantReference) {
                 getDataForModel(input)
                .flatMap(createModel())
                .map(saveToFile(output))
                .map(__-> CloudObjectReferenceFactory.moveStore(assistantReference, output))
                .map(copyAssistantDefinitions(assistantReference));
    }
    private Function<CloudObjectReference, CloudStoreResult> copyAssistantDefinitions(final CloudObjectReference input) {
        return output-> cloudCopyable.copy(input, output);
    }

    private Function<ByteArrayOutputStream, CloudStoreResult> saveToFile(final CloudObjectReference output) {
        return s->cloudStorable.store(output, s);
    }

    private Optional<InputStream> getDataForModel(final CloudObjectReference input) {
        return Optional.of(cloudLoadable
                .retrieve(input, s->s)
                .orElseThrow(()->new CloudException("Unable to load model")));
    }

    Function<InputStream, Optional<ByteArrayOutputStream>> createModel() {
        return inputStream -> {
            try(inputStream){
                return Optional.of(new HandleResult<>(modelMaker.transform(inputStream))
                        .calling()
                        .orElseThrow(UNABLE_TO_CREATE_A_MODEL));
            } catch (final IOException e) {
                return Optional.empty();
            }
        };

    }
}
