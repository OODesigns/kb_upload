package general;

public interface Retrievable<T, U> {
    U retrieve(T t);
}
