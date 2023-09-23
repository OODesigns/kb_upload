package kb_upload;

import java.util.Optional;

public interface Validator<T, U>{
    Optional<Validated> validate(T t, U u);
}
