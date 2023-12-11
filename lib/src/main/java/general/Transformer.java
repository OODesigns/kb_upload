package general;

@FunctionalInterface
public interface Transformer<T, U>{
    U transform(T input);
}
