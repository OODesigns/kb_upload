package aws;

import com.amazonaws.services.lambda.runtime.Context;
import kb_upload.JSON;
import kb_upload.JSONData;
import kb_upload.Retrievable;
import kb_upload.Transformer2_1;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class S3JSONFileDataTransformer implements Transformer2_1<Context, S3Object, JSON> {
    private static final String UNABLE_TO_LOAD_FILE = "Unable to load file from bucket: %s and key: %s";
    public static final String MISSING_DATA_WHEN_CREATING_JSON = "UnExpected missing Data when creating JSON";
    private final Retrievable<S3Object, Optional<InputStream>> fileloader;

    public S3JSONFileDataTransformer(final Retrievable<S3Object, Optional<InputStream>> fileloader) {
        this.fileloader = fileloader;
    }

    public JSON transform(final Context context, final S3Object s3Object) {
        return getData(context, s3Object)
                    .map(JSONData::new)
                    .orElseThrow(()->new RuntimeException(MISSING_DATA_WHEN_CREATING_JSON));
    }

    private Optional<String> getData(final Context context, final S3Object s3Object) {
        try(final InputStream filestream =
                    fileloader.retrieve(s3Object)
                              .orElseThrow(() -> throwUnableToLoadFile(context, s3Object))){
            return Optional.of(IOUtils.toString(filestream, StandardCharsets.UTF_8));
        } catch (final IOException e) {
            return Optional.empty();
        }
    }

    private s3Exception throwUnableToLoadFile(final Context context, final S3Object s3Object) {
        return new s3Exception(context, String.format(UNABLE_TO_LOAD_FILE,
                s3Object.getBucketName(), s3Object.getKeyName()));
    }
}