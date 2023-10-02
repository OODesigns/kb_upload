package kb_upload;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class JSonArrayToListTest {

    @Test
    void transformJSONToList(){

        final String jsonData = """
            {
            "person"J:[
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

        final List<String> transformed = transformer.transform(jsonData).orElse(null);

        assertThat(transformed).isNotNull();
        assertThat(transformed.get(0)).isEqualTo("John Doe Doe Doe");
        assertThat(transformed.get(1)).isEqualTo("Jane Smith");
    }

}