package com.oodesigns.ai.support;

import java.util.List;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LogCapture implements AutoCloseable {
    private final Logger logger;
    private final LogCapturingHandler logCapturingHandler;

    public LogCapture(final String logName) {
        logger = Logger.getLogger(logName);
        logCapturingHandler = new LogCapturingHandler();
        logger.addHandler(logCapturingHandler);
    }

    public LogCapture(final Class<?> classType) {
        this(classType.getName());
    }

    @Override
    public void close() {
        logger.removeHandler(logCapturingHandler);
        // added to get test code coverage, does nothing in this case.
        logCapturingHandler.flush();
        logCapturingHandler.close();
    }

    public List<LogRecord> getLogs(){
        return logCapturingHandler.getCapturedLogRecords();
    }
}
