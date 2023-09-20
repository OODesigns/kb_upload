package kb_upload;

import com.sun.nio.sctp.InvalidStreamException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class FileLoader implements Loader<String>{

    private final String fileName;

    public FileLoader(final String filename) {
        this.fileName = filename;
    }

    @Override
    public String load() {
        final ClassLoader classLoader = JSONData.class.getClassLoader();
        try (final InputStream inputStream = classLoader.getResourceAsStream(fileName)) {
            return new String(Objects.requireNonNull(inputStream).readAllBytes(), StandardCharsets.UTF_8);
         } catch (final IOException | InvalidStreamException | NullPointerException e) {
            throw new RuntimeException(e);
        }
    }
}
