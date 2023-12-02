package json;

import general.Mappable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class JSONArrayToListResult implements Mappable<List<String>, String, String> {

    private final List<String> value;

    public JSONArrayToListResult(final List<String> value) {
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
