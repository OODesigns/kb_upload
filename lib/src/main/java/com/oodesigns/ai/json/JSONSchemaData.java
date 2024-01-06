package com.oodesigns.ai.json;

public class JSONSchemaData extends JSONData implements JSONSchema {
    private static final String MISSING_SCHEMA_REFERENCE_IS_JSON_FILE = "Missing Schema reference in JSON File";
    private static final String SCHEMA = "$schema";
    private static final String JSON_SCHEMA_ORG =  "http://json-schema.org/draft/2020-12/schema#";

    public JSONSchemaData(final String data) {
        super(data);
        if(!(jsonObject.has(SCHEMA) && jsonObject.getString(SCHEMA).contains(JSON_SCHEMA_ORG))){
          throw new JSONSchemaException(MISSING_SCHEMA_REFERENCE_IS_JSON_FILE);
        }
    }
}
