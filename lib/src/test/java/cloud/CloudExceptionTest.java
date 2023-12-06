package cloud;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CloudExceptionTest {

    @Test
    void testCloudException() {
        final CloudException ce = new CloudException("test");
        assertEquals("test", ce.getMessage());
    }

}