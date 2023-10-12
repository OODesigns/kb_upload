package aws;

import com.amazonaws.services.lambda.runtime.Context;

public class s3Exception extends RuntimeException{

    public s3Exception(final Context context, final String message) {
        super(message);
        context.getLogger().log(message);
    }
}
