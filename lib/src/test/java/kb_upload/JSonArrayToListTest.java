package kb_upload;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class JSonArrayToListTest {

    @Test
    void transformJSONToList(){

        final String jsonData = """
            {
            "person":[
                {
                    "firstName": "John",
                    "lastName": "Doe Doe Doe"
                },
                {
                    "firstName": "Jane",
                    "lastName": "Smith"
                }
            ]}""";

        final Transformer<String, Optional<List<String>>> transformer = new JSonArrayToList("person");

        transformer.transform(jsonData)
                .ifPresentOrElse(t->{
                    assertThat(t.get(0)).isEqualTo("John Doe Doe Doe");
                    assertThat(t.get(1)).isEqualTo("Jane Smith");}
                    ,()->fail("Expected to get data but got nothing")
                );
    }

    @Test
    void failToTransformJSONToList(){

        final String jsonData = """
            {
            "persons":[
                {
                    "firstName": "John",
                    "lastName": "Doe Doe Doe"
                },
                {
                    "firstName": "Jane",
                    "lastName": "Smith"
                }
            ]}""";

        final Transformer<String, Optional<List<String>>> transformer = new JSonArrayToList("person");

        transformer.transform(jsonData)
                .ifPresent(__->fail("Expected to get nothing but got something"));
    }


}