package kb_upload;

public class JSONSchemaData extends JSONData implements JSONSchema {
    private static final String MISSING_SCHEMA_REFERENCE_IS_JSON_FILE = "Missing Schema reference in JSON File";
    private static final String $_SCHEMA = "$schema";

    public JSONSchemaData(final String data) {
        super(data);
        if(!jsonObject.has($_SCHEMA)){
          throw new RuntimeException(MISSING_SCHEMA_REFERENCE_IS_JSON_FILE);
        }
    }
}
