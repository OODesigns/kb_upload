package aws;

public record S3FileSaverErrorState(String message) implements S3FileSaverState {
    @Override
    public String toString() {
        return "S3FileSaverErrorState: " +
                "message='" + message + '\'';
    }
}
