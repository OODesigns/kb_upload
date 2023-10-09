package kb_upload;

public interface Storable <T, U, V>{
    V store(T t, U u);
}
