package kb_upload;

import java.util.List;

public record Validated(ValidatedState state, List<String> messages) implements Validation {}
