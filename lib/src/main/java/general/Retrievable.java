package general;

@FunctionalInterface
public interface Retrievable<T, U> {
    U retrieve(T t);
}
