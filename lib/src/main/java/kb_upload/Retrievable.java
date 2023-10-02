package kb_upload;

public interface Retrievable<T, U> {
    U retrieve(T t);
}
