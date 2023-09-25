package kb_upload;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSonArrayToList implements Transformer<String, Optional<List<String>>> {
    public static final String SPACE = " ";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final String arrayName;


    public JSonArrayToList(final String arrayName) {
        this.arrayName = arrayName;
    }

    @Override
    public Optional<List<String>> transform(final String jsonText) {
        return getArray(jsonText)
                .filter(JsonNode::isArray)
                .map(getJsonArrayItems());
    }

    private Function<JsonNode, List<String>> getJsonArrayItems() {
        return arrayNode -> StreamSupport.stream(arrayNode.spliterator(), false)
                .map(getJsonNodeAsString())
                .collect(Collectors.toList());
    }

    private Function<JsonNode, String> getJsonNodeAsString() {
        return node -> StreamSupport.stream(node.spliterator(), false)
                .map(JsonNode::asText)
                .collect(Collectors.joining(SPACE));
    }

    private Optional<JsonNode> getArray(final String json) {
        try {
            return Optional.of(objectMapper.readTree(json).get(arrayName));
        } catch (final JsonProcessingException ex) {
            return Optional.empty();
        }
    }

}
