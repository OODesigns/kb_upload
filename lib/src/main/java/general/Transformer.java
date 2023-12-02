package general;

public interface Transformer<T, U>{
    U transform(T input);
}
