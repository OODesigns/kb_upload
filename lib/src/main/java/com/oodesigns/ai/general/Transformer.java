package com.oodesigns.ai.general;

@FunctionalInterface
public interface Transformer<T, U>{
    U transform(T input);
}
