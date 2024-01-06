package com.oodesigns.ai.general;
@FunctionalInterface
public interface Storable <T, U, V>{
    V store(T t, U u);
}
