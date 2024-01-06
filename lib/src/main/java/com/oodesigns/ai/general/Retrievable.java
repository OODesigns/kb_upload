package com.oodesigns.ai.general;

@FunctionalInterface
public interface Retrievable<T, U> {
    U retrieve(T t);
}
