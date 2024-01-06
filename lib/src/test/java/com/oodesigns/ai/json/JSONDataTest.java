package com.oodesigns.ai.json;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JSONDataTest {

    static private final String validJSON = """           
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

    static private final String inValidJSON = """           
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
        assertThrows(JSONException.class, ()-> new JSONData(inValidJSON));
    }

    @Test
    void validJsonNoException(){
        assertDoesNotThrow(()->new JSONData(validJSON));
    }

    @Test
    void validJSONGetReturnsSame(){
        assertThat(new JSONData(validJSON).get()).contains(validJSON);
    }

}