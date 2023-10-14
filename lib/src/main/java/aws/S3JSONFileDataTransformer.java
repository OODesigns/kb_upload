package aws;

import com.amazonaws.services.lambda.runtime.Context;
import kb_upload.JSON;
import kb_upload.JSONData;
import kb_upload.Retrievable;
import kb_upload.Transformer2_1;

import java.util.Optional;

public class S3JSONFileDataTransformer implements Transformer2_1<Context, S3Object, JSON> {
    private static final String UNABLE_TO_LOAD_FILE = "Unable to load file from bucket: %s and key: %s";
    private final Retrievable<S3Object, Optional<String>> fileloader;

    public S3JSONFileDataTransformer(final Retrievable<S3Object, Optional<String>> fileloader) {
        this.fileloader = fileloader;
    }

    public JSON transform(final Context context, final S3Object s3Object) {
        return  new JSONData(fileloader.retrieve(s3Object).orElseThrow(() -> throwUnableToLoadFile(context, s3Object)));
    }

    private s3Exception throwUnableToLoadFile(final Context context, final S3Object s3Object) {
        return new s3Exception(context, String.format(UNABLE_TO_LOAD_FILE,
                s3Object.getBucketName(), s3Object.getKeyName()));
    }
}