package aws;

import com.amazonaws.services.lambda.runtime.Context;
import kb_upload.JSON;
import kb_upload.Transformer2_1;

public interface S3ObjectToJSON extends Transformer2_1<Context, S3Object, JSON> {
}
