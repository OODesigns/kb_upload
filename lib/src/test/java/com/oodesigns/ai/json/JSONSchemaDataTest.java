package com.oodesigns.ai.json;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JSONSchemaDataTest {

    static private final String validJSONNotSchemaFile = """           
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

    static private final String validSchemaFile = """           
                {
                 "$schema": "http://json-schema.org/draft/2020-12/schema#",
                  "utterance": [
                    {"name": "test name 1",
                    "entries":"test entry 1"},
                    {"name":"test name 2",
                    "entries":"test entry 2"
                    }
                   ]
                }
                """;

    @Test
    void nonSchemaFile() {assertThrows(JSONSchemaException.class, ()->new JSONSchemaData(validJSONNotSchemaFile));}

    @Test
      void validSchemaFile(){assertDoesNotThrow(()->new JSONSchemaData(validSchemaFile));}

}