package com.oodesigns.ai.json;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JSONDataTest {

    private static final String VALID_JSON = """           
                {
                 "$schema": "knowledgeSchema.json",
                  "utterance": [
                    {"name": "test name 1",
                    "entries":"test entry 1"},
                    {"name":"test name 2",
                    "entries":"test entry 2"
                    }
                   ]
                }
                """;

    private static final String IN_VALID_JSON = """           
                {
                 "$schema": "knowledgeSchema.json",
                  "utterance":
                    {"name": "test name 1",
                    "entries":"test entry 1"},
                    {"name":"test name 2",
                    "entries":"test entry 2"
                    }
                   ]
                }
                """;

    @Test
    void invalidJsonThrows(){
        assertThrows(JSONException.class, ()-> new JSONData(IN_VALID_JSON));
    }

    @Test
    void validJsonNoException(){
        final JSONObject data = new JSONObject(VALID_JSON);
        data.getJSONArray("utterance");

        assertDoesNotThrow(()->new JSONData(VALID_JSON));
    }

    @Test
    void validJSONGetReturnsSame(){
        assertThat(new JSONData(VALID_JSON).get()).contains(VALID_JSON);
    }

}