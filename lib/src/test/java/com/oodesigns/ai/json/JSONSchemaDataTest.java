package com.oodesigns.ai.json;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JSONSchemaDataTest {

    private static final String VALID_JSON_NOT_SCHEMA_FILE = """           
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

    private static final String VALID_SCHEMA_FILE = """           
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
    void nonSchemaFile() {assertThrows(JSONSchemaException.class, ()->new JSONSchemaData(VALID_JSON_NOT_SCHEMA_FILE));}

    @Test
      void validSchemaFile(){assertDoesNotThrow(()->new JSONSchemaData(VALID_SCHEMA_FILE));}

}