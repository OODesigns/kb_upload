package cloud;

import assistant_configuration_creator.HandleResult;

import java.io.ByteArrayOutputStream;

public class CloudStore implements CloudStorable{
    private static final String ERROR_UNABLE_TO_SAVE_FILE = "Error unable to save file: %s";
    private final CloudStorable cloudStorable;
    public CloudStore(final CloudStorable cloudStorable) {
        this.cloudStorable = cloudStorable;
    }

    @Override
    public CloudStoreResult store(final CloudObjectReference cloudObjectReference, final ByteArrayOutputStream dataStream) {
        return new HandleResult<>(cloudStorable.store(cloudObjectReference, dataStream))
                .calling()
                .orElseThrow(ERROR_UNABLE_TO_SAVE_FILE);
    }
}
