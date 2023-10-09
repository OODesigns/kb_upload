package kb_upload;
import java.util.Optional;
import java.util.function.Function;

public interface mappable<T, U, V> {
    Optional<V> map(Function<T, U> mapper);
}
