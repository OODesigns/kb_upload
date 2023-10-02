package kb_upload;

import java.util.Optional;

public interface Validator<T, U, V>{
    Optional<V> validate(T t, U u);
}
