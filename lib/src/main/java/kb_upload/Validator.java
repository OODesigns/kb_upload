package kb_upload;

public interface Validator<T, U>{
    Validated validate(T t, U u);
}
