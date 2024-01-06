package com.oodesigns.ai.assistant_configuration_creator;
import com.oodesigns.ai.cloud.*;
import com.oodesigns.ai.general.Mappable;
import com.oodesigns.ai.general.Transformer;
import com.oodesigns.ai.json.JSON;
import com.oodesigns.ai.transformer.StringToStreamTransformer;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;


public class TransformationHandler {
    private static final String UNABLE_TO_TRANSFORM_DATA = "Unable to transform data";
    private final CloudObjectToJSON cloudObjectToJSON;
    private final Transformer<JSON, Mappable<List<String>, String, String>> jsonTransformer;
    private final CloudStorable cloudStorable;
    private final CloudCopyable cloudCopyable;
    private final Transformer<String, Optional<ByteArrayOutputStream>> stringToStreamTransformer
            = new StringToStreamTransformer(ByteArrayOutputStream::new);

    public TransformationHandler(final CloudObjectToJSON cloudObjectToJSON,
                                 final Transformer<JSON, Mappable<List<String>, String, String>> jsonTransformer,
                                 final CloudStorable cloudStorable,
                                 final CloudCopyable cloudCopyable) {
        this.cloudObjectToJSON = cloudObjectToJSON;
        this.jsonTransformer = jsonTransformer;
        this.cloudStorable = cloudStorable;
        this.cloudCopyable = cloudCopyable;
    }

    public void handleRequest(final CloudObjectReference input, final CloudObjectReference output) {
        Optional.of(getCategoriesAndDocuments(input))
                .flatMap(this::transformDataForModalMaker)
                .map(saveToFile(output))
                .map(__ -> CloudObjectReferenceFactory.moveStore(input, output))
                .map(copyAssistantDefinitions(input));
    }

    private Function<CloudObjectReference, CloudStoreResult> copyAssistantDefinitions(final CloudObjectReference input) {
        return output -> cloudCopyable.copy(input, output);
    }

    private JSON getCategoriesAndDocuments(final CloudObjectReference input) {
        return cloudObjectToJSON.transform(input);
    }

    private Function<ByteArrayOutputStream, CloudStoreResult> saveToFile(final CloudObjectReference output) {
        return data -> cloudStorable.store(output, data);
    }

    private Optional<ByteArrayOutputStream> transformDataForModalMaker(final JSON json) {
        return Optional.of(jsonTransformer.transform(json)
                .map(newLineForEachEntry())
                .flatMap(stringToStreamTransformer::transform)
                .orElseThrow(() -> new CloudException(UNABLE_TO_TRANSFORM_DATA)));
    }

    private Function<List<String>, String> newLineForEachEntry() {
        return l -> String.join("\n", l);
    }
}
