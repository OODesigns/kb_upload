package com.oodesigns.ai.general;

public interface ResultState<T, U> extends Callable<T>,ThrowableElse<U, T, RuntimeException>{}
