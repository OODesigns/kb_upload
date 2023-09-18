package kb_upload;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONData implements JSON {
    public static final String INVALID_JSON_FORMAT_S = "Invalid Json format: %s";
    private final String data;

    public JSONData(final String data) {
        try {
            jsonObject = new JSONObject(data);
            this.data = data;
        } catch (final JSONException e){
            throw new RuntimeException(String.format(INVALID_JSON_FORMAT_S, e.getMessage()));
        }
    }

    @Override
    public String get() {
        return data;
    }
}
