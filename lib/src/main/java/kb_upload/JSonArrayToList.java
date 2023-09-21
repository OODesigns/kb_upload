package kb_upload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

public class JSonArrayToList implements Transformer<String, List<String>> {
    public static final String SPACE = " ";
    private final String arrayName;

    public JSonArrayToList(final String arrayName) {
        this.arrayName = arrayName;
    }

    private String concatenate(final JsonNode jsonNode) {
        final StringBuilder result = new StringBuilder();
        jsonNode.elements().forEachRemaining(valueNode -> result.append(valueNode.asText()).append(SPACE));
        return result.toString().trim();
    }


    private Optional<JsonNode> getArray(final String json) {
        try {
            return Optional.of(new ObjectMapper().readTree(json).get(arrayName));
        } catch (final JsonProcessingException ex) {
            return Optional.empty();
        }
    }


    @Override
    public List<String> transform(final String json) {
        return getArray(json)
                .filter(JsonNode::isArray)
                .map(this::concatenate)
                .stream()
                .toList();
    }
}
