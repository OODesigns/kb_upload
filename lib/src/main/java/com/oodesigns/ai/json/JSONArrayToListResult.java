package com.oodesigns.ai.json;

import com.oodesigns.ai.general.Mappable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record JSONArrayToListResult(List<String> value) implements Mappable<List<String>, String, String> {

    @Override
    public Optional<String> map(final Function<List<String>, String> mapper) {
        if (value.isEmpty()) {
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
