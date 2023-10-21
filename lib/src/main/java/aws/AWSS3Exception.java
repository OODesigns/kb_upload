package aws;

import com.amazonaws.services.lambda.runtime.Context;

public class AWSS3Exception extends RuntimeException{

    public AWSS3Exception(final Context context, final String message) {
        super(message);
        context.getLogger().log(message);
    }
}
