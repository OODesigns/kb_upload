package kb_upload;

import com.sun.nio.sctp.InvalidStreamException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class FileLoader{

    public static final String RESOURCE_S_NOT_FOUND = "Resource %s not found";
    private final String data;

    public FileLoader(final String fileName) {

        final ClassLoader classLoader = JSONData.class.getClassLoader();

        try (final InputStream inputStream = classLoader.getResourceAsStream(fileName)) {
            if (inputStream == null)
                throw new IOException(String.format(RESOURCE_S_NOT_FOUND, fileName));

            data = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        } catch (final IOException | InvalidStreamException | NullPointerException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return data;
    }
}
