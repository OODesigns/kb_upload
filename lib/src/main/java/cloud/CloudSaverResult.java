package cloud;

public abstract class CloudSaverResult implements CloudSaverState<CloudSaverResult> {
    private final String message;

    protected CloudSaverResult(final String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
