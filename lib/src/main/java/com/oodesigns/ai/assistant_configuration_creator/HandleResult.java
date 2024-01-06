package com.oodesigns.ai.assistant_configuration_creator;
import com.oodesigns.ai.cloud.CloudException;
import com.oodesigns.ai.general.ResultState;

import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HandleResult<T, U> {
    private static final Logger logger = Logger.getLogger(HandleResult.class.getName());
    private static final String RESULT = "RESULT: %s";
    private final ResultState<T, U> resultResultStateObject;
    public HandleResult(final ResultState<T, U> resultResultStateObject) {
        this.resultResultStateObject = resultResultStateObject;
    }

    public HandleResult<T, U> calling() {
        resultResultStateObject.calling(logResult());
        return this;
    }

    public U orElseThrow() {
        return resultResultStateObject.orElseThrow(newException("%s"));
    }

    public U orElseThrow(final String errorFormat){
        return resultResultStateObject.orElseThrow(newException(errorFormat));
    }

    private UnaryOperator<T> logResult() {
        return v -> { logger.log(Level.INFO, String.format(RESULT, v)); return v; };
    }

    private Function<T, RuntimeException> newException(final String errorFormat) {
        return m -> new CloudException(String.format(errorFormat,m.toString()));
    }
}
