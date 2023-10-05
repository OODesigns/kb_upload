package aws;

public class KeyName implements KeyNameProvider {
    private final String filename;

    public KeyName(final String string) {
        this.filename = string;
    }

    @Override
    public String get() {
        return filename;
    }
}
