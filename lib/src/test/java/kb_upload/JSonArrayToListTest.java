package kb_upload;

import org.junit.jupiter.api.Test;
import java.util.Objects;

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

        final JSonArrayToList transformer = new JSonArrayToList("person");

        transformer.transform(new JSONData(jsonData))
                .map(Objects::toString)
                .ifPresentOrElse(t->{
                            assertThat(t).contains("John Doe Doe Doe");
                            assertThat(t).contains("Jane Smith");
                        },()->fail("Expected to get data but got nothing")
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

        final JSonArrayToList transformer = new JSonArrayToList("person");
        transformer.transform(new JSONData(jsonData))
                .map(Object::toString)
                .ifPresent(__->fail("Expected to get nothing but got something"));
    }
}