package assistant_configuration_creator;
import cloud.*;
import general.Mappable;
import general.Transformer;
import json.JSON;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransformationHandler {
    private static final String UNABLE_TO_TRANSFORM_DATA = "Unable to transform data";
    private final CloudObjectToJSON cloudObjectToJSON;
    private final Transformer<JSON, Mappable<List<String>, String, String>> jsonTransformer;
    private final CloudStorable cloudStorable;
    private final CloudCopyable cloudCopyable;

    private static final Logger logger = Logger.getLogger(TransformationHandler.class.getName());

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
                .map(__->CloudObjectReferenceFactory.moveStore(input, output))
                .map(copyAssistantDefinitions(input));
    }

    private Function<CloudObjectReference, CloudStoreResult> copyAssistantDefinitions(final CloudObjectReference input) {
        return output-> cloudCopyable.copy(input, output);
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
                .flatMap(transformToStream())
                .orElseThrow(()-> new CloudException(UNABLE_TO_TRANSFORM_DATA)));
    }

    private Function<List<String>, String> newLineForEachEntry() {
        return l -> String.join("\n", l);
    }

    private Function<String, Optional<ByteArrayOutputStream>> transformToStream() {
        return s -> {
            try {
                final byte[] bytes = s.toLowerCase().getBytes();
                return Optional.of(new ByteArrayOutputStream(bytes.length) {{write(bytes);}});
            } catch (final IOException e) {
                logger.log(Level.SEVERE, e.getMessage());
                return Optional.empty();
            }
        };
    }

}
