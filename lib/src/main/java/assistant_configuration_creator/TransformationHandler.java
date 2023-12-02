package assistant_configuration_creator;
import cloud.*;
import general.Mappable;
import general.Retrievable;
import general.Transformer;
import json.JSON;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class TransformationHandler {
    private static final String UNABLE_TO_TRANSFORM_DATA = "Unable to transform data: %s";
    private final CloudObjectToJSON cloudObjectToJSON;
    private final Retrievable<CloudObjectReference, Optional<InputStream>> fileLoader;
    private final Transformer<JSON, Mappable<List<String>, String, String>> jsonTransformer;
    private final CloudStorable cloudStorable;

    public TransformationHandler(final Retrievable<CloudObjectReference, Optional<InputStream>> fileLoader,
                                 final Transformer<JSON, Mappable<List<String>, String, String>> jsonTransformer,
                                 final CloudStorable cloudStorable) {
        this.cloudObjectToJSON = new CloudJSONFileDataTransformer(new CloudLoad<>(fileLoader));
        this.fileLoader = fileLoader;
        this.jsonTransformer = jsonTransformer;
        this.cloudStorable = cloudStorable;
    }

    public void handleRequest(final CloudObjectReference input, final CloudObjectReference output) {
        Optional.of(getCategoriesAndDocuments(input))
                .flatMap(this::transformDataForModalMaker)
                .map(saveToFile(output))
                .flatMap(__-> getCompleteFileForAssistantDefinitions(input))
                .map(saveToFile(createDestinationReference(input, output)));
    }

    private CloudObjectReference createDestinationReference(final CloudObjectReference input, final CloudObjectReference output) {
        return new CloudObjectReference() {
            @Override
            public String getStoreName() {
                return output.getStoreName();
            }

            @Override
            public String getObjectName() {
                return input.getObjectName();
            }
        };
    }

    private Optional<ByteArrayOutputStream> getCompleteFileForAssistantDefinitions(final CloudObjectReference input) {
        return new CloudLoad<ByteArrayOutputStream>(fileLoader).retrieve(input, this::convertInputStreamToByteArrayOutputStream);
    }

    public ByteArrayOutputStream convertInputStreamToByteArrayOutputStream(final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
        return byteArrayOutputStream;
    }

    private JSON getCategoriesAndDocuments(final CloudObjectReference input) {
        return cloudObjectToJSON.transform(input);
    }

    private Function<ByteArrayOutputStream, CloudSaverResult> saveToFile(final CloudObjectReference output) {
        return data -> cloudStorable.store(output, data);
    }

    private Optional<ByteArrayOutputStream> transformDataForModalMaker(final JSON json) {
        return jsonTransformer.transform(json)
                .map(newLineForEachEntry())
                .flatMap(transformToStream());
    }

    private static Function<List<String>, String> newLineForEachEntry() {
        return l -> String.join("\n", l);
    }

    private static Function<String, Optional<ByteArrayOutputStream>> transformToStream() {
        return s -> {
            try {
                final byte[] bytes = s.getBytes();
                return Optional.of(new ByteArrayOutputStream(bytes.length) {{write(bytes);}});
            } catch (final IOException e) {
                throw new CloudException(String.format(UNABLE_TO_TRANSFORM_DATA, e.getMessage()));
            }
        };
    }

}
