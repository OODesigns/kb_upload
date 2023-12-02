package cloud;

public abstract class CloudStreamSaverResult implements CloudStreamSaverState<CloudStreamSaverResult>{
    private final String message;

    protected CloudStreamSaverResult(final String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
