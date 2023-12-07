package json;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import general.Mappable;
import general.Transformer;

public class JSONArrayToList implements Transformer<JSON, Mappable<List<String>, String, String>> {
    public static final String SPACE = " ";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final String arrayName;

    private static final Logger logger = Logger.getLogger(JSONArrayToList.class.getName());


    public JSONArrayToList(final String arrayName) {
        this.arrayName = arrayName;
    }

    @Override
    public Mappable<List<String>, String, String> transform(final JSON json) {
        return getArray(json.get())
                .filter(JsonNode::isArray)
                .map(getJsonArrayItems())
                .map(JSONArrayToListResult::new)
                .orElseGet(()->new JSONArrayToListResult(List.of()));
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
        } catch (final JsonProcessingException | NullPointerException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            return Optional.empty();
        }
    }
}
