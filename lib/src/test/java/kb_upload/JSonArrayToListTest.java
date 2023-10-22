package kb_upload;

import org.junit.jupiter.api.Test;

import java.util.List;
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

          final String expectedResult = "John Doe Doe Doe\n" +
                                        "Jane Smith";

        final JSonArrayToList transformer = new JSonArrayToList("person");

        final Mappable<List<String>, String, String> transform = transformer.transform(new JSONData(jsonData));

        transform.map(l->String.join("\n",l))
                .ifPresentOrElse(t->assertThat(t).contains(expectedResult),
                        ()->fail("Expected to get data but got nothing")
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