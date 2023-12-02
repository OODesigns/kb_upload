package cloud;
import json.JSON;
import json.JSONData;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class CloudJSONFileDataTransformer implements CloudObjectToJSON {
    private static final String MISSING_DATA_WHEN_CREATING_JSON = "UnExpected missing data when creating JSON";
    private final CloudLoad<String> cloudLoad;

    public CloudJSONFileDataTransformer(final CloudLoad<String> cloudLoad) {
        this.cloudLoad = cloudLoad;
    }

    public JSON transform(final CloudObjectReference cloudObjectReference) {
        return getData(cloudObjectReference)
                    .map(JSONData::new)
                    .orElseThrow(()->new RuntimeException(MISSING_DATA_WHEN_CREATING_JSON));
    }

    private Optional<String> getData(final CloudObjectReference cloudObjectReference) {
        return cloudLoad.retrieve(cloudObjectReference, this::getString);
    }

    private String getString(final InputStream inputStream) throws IOException{
        return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    }
}