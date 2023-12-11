package general;

@FunctionalInterface
public interface Validator<T, U, V>{
    V validate(T t, U u);
}
