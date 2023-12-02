package general;
import java.util.Optional;
import java.util.function.Function;

@FunctionalInterface
public interface Mappable<T, U, V> {
    Optional<V> map(Function<T, U> mapper);
}
