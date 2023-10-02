package kb_upload;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class FileLoaderTest {

    @Test
    void unableToLoadFileThrows(){
        assertThrows(FileLoaderException.class, ()->new FileLoader("NoFile.txt"));
    }

    @Test
    void loadAndReadText(){
        final FileLoader fileLoader = new FileLoader("testfile.txt");
        assertThat(fileLoader.toString()).contains("some test data");
    }

}