package aws;

import com.amazonaws.services.lambda.runtime.Context;
import kb_upload.JSON;
import kb_upload.JSONData;
import kb_upload.Retrievable;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class S3JSONFileDataTransformer implements S3ObjectToJSON {
    private static final String UNABLE_TO_LOAD_FILE = "Unable to load file from bucket: %s and key: %s";
    private static final String MISSING_DATA_WHEN_CREATING_JSON = "UnExpected missing Data when creating JSON";
    private final Retrievable<S3ObjectReference, Optional<InputStream>> fileLoader;

    public S3JSONFileDataTransformer(final Retrievable<S3ObjectReference, Optional<InputStream>> fileLoader) {
        this.fileLoader = fileLoader;
    }

    public JSON transform(final Context context, final S3ObjectReference s3ObjectReference) {
        return getData(context, s3ObjectReference)
                    .map(JSONData::new)
                    .orElseThrow(()->new RuntimeException(MISSING_DATA_WHEN_CREATING_JSON));
    }

    private Optional<String> getData(final Context context, final S3ObjectReference s3ObjectReference) {
        try(final InputStream filestream =
                    fileLoader.retrieve(s3ObjectReference)
                              .orElseThrow(() -> throwUnableToLoadFile(context, s3ObjectReference))){
            return Optional.of(IOUtils.toString(filestream, StandardCharsets.UTF_8));
        } catch (final IOException e) {
            context.getLogger().log(e.getMessage());
            return Optional.empty();
        }
    }

    private AWSS3Exception throwUnableToLoadFile(final Context context, final S3ObjectReference s3ObjectReference) {
        return new AWSS3Exception(context, String.format(UNABLE_TO_LOAD_FILE,
                s3ObjectReference.getBucketName(), s3ObjectReference.getKeyName()));
    }
}