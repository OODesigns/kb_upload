package com.oodesigns.ai.support;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class LogCaptureTest {
    private static final Logger logger = Logger.getLogger("TestLogger");
    private LogCapture logCapture;

    @BeforeEach
    void setUp() {
        // Create a LogCapture instance for testing, with a logger name
        logCapture = new LogCapture("TestLogger");
    }

    @AfterEach
    void tearDown() {
        // Ensure that the LogCapture instance is closed after each test
        logCapture.close();
    }

    @Test
    void testLogging() {
        // Trigger some logging
        logger.info("This is an info message.");
        logger.warning("This is a warning message.");

        // Retrieve the captured log records
        final List<LogRecord> capturedLogs = logCapture.getLogs();

        // Perform assertions on captured log records
        assertEquals(2, capturedLogs.size());

        // Check log record details
        final LogRecord infoRecord = capturedLogs.get(0);
        assertEquals(Level.INFO, infoRecord.getLevel());
        assertEquals("This is an info message.", infoRecord.getMessage());

        final LogRecord warningRecord = capturedLogs.get(1);
        assertEquals(Level.WARNING, warningRecord.getLevel());
        assertEquals("This is a warning message.", warningRecord.getMessage());
    }

}