package kb_upload;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class JsonTransformationResult implements Mappable<List<String>, String, String> {

    private final List<String> value;

    public JsonTransformationResult(final List<String> value) {
        this.value = value;
    }

    @Override
    public Optional<String> map(final Function<List<String>, String> mapper) {
        if(value.isEmpty()){
            return Optional.empty();
        } else {
            return Optional.ofNullable(mapper.apply(value));
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
