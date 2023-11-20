package aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import kb_upload.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@MockitoSettings
class AWSContextDecoratorTest {
    @Mock
    private Context mockContext;
    @Mock
    private Validator<JSONSchema, JSON, ValidationResult> mockValidator;

    private AWSContextDecorator decorator;

    @BeforeEach
    void setUp(@Mock final LambdaLogger lambdaLogger) {
        when(mockContext.getLogger()).thenReturn(lambdaLogger);
        decorator = AWSContextDecorator.of(mockContext, mockValidator);
    }

    @Test
    void testValidate_Success() {
        // Mock the behavior of the validator to return a successful result
        when(mockValidator.validate(any(), any())).thenReturn(new ValidatedStateOK());

        final ValidationResult result = decorator.validate(new JSONSchemaData("{\"$schema\":\"http://json-schema.org/draft/2020-12/schema#\"}"), ()->"{}");

        // Verify that the result is as expected
        assertThat(result).isInstanceOf(ValidatedStateOK.class);
        assertThat(result.getMessage()).contains("Validation State OK");
    }

    @Test
    void testValidate_Failure() {
        // Mock the behavior of the validator to return a failed result

        when(mockValidator.validate(any(), any())).thenReturn(new ValidatedStateError("error"));

        assertThrows(AWSS3Exception.class, ()->decorator.validate(new JSONSchemaData("{\"$schema\":\"http://json-schema.org/draft/2020-12/schema#\"}"), ()->"{}"));
    }
}

