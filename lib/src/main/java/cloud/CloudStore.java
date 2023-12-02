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
    public CloudSaverResult store(final CloudObjectReference cloudObjectReference, final ByteArrayOutputStream dataStream) {
        final CloudSaverState<CloudSaverResult> storeResult = cloudStorable.store(cloudObjectReference, dataStream);

        return new HandleResult<CloudSaverResult, CloudSaverResult>()
                .calling(storeResult)
                .orElseThrow(storeResult, ERROR_UNABLE_TO_SAVE_FILE);
    }
}
