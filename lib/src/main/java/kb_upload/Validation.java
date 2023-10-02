package kb_upload;

import java.util.List;

public interface Validation {
    ValidatedState state();

    List<String> messages();
}
