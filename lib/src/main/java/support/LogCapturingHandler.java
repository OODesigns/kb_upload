package support;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class LogCapturingHandler extends Handler {
    private final List<LogRecord> capturedLogRecords = new ArrayList<>();

    @Override
    public void publish(final LogRecord record) {
        capturedLogRecords.add(record);
    }

    @Override
    public void flush() {
        // No need to implement
    }

    @Override
    public void close() throws SecurityException {
        // No need to implement
    }

    public List<LogRecord> getCapturedLogRecords() {
        return capturedLogRecords;
    }
}
