package com.oodesigns.ai.json;
import org.json.JSONObject;

public class JSONData implements JSON {
    public static final String INVALID_JSON_FORMAT_S = "Invalid Json format: %s";
    private final String data;
    protected final JSONObject jsonObject;

    public JSONData(final String data) {
        try {
            jsonObject = new JSONObject(data);
            this.data = data;
        } catch (final org.json.JSONException e){
            throw new JSONException(String.format(INVALID_JSON_FORMAT_S, e.getMessage()));
        }
    }

    @Override
    public String get() {
        return data;
    }
}
