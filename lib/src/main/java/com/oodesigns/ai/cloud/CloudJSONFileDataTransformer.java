package com.oodesigns.ai.cloud;
import com.oodesigns.ai.json.JSON;
import com.oodesigns.ai.json.JSONData;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class CloudJSONFileDataTransformer implements CloudObjectToJSON {
    public static final String UNABLE_TO_TRANSFORM_FILE_TO_JSON = "Unable to transform file to JSON";
    private final CloudLoadable<String> cloudLoad;

    public CloudJSONFileDataTransformer(final CloudLoadable<String> cloudLoad) {
        this.cloudLoad = cloudLoad;
    }

    public JSON transform(final CloudObjectReference cloudObjectReference) {
        return getData(cloudObjectReference)
                .map(JSONData::new)
                .orElseThrow(()->new CloudException(UNABLE_TO_TRANSFORM_FILE_TO_JSON));
    }

    private Optional<String> getData(final CloudObjectReference cloudObjectReference) {
        return cloudLoad.retrieve(cloudObjectReference, this::getString);
    }

    private String getString(final InputStream inputStream) throws IOException{
        return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    }
}