package kb_upload;

public interface Transformer <T, U>{
    U transform(T input);
}
