package function;

import java.util.ArrayList;
import java.util.List;

public class TestLogger implements com.amazonaws.services.lambda.runtime.LambdaLogger {
    private final List<String> data = new ArrayList<>();
    @Override
    public void log(final String message) {
        data.add(message);
    }

    @Override
    public void log(final byte[] message) {}

    @Override
    public String toString() {
        return data.toString();
    }
}
