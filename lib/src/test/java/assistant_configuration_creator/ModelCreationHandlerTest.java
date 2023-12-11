package assistant_configuration_creator;

import cloud.CloudCopyable;
import cloud.CloudLoadable;
import cloud.CloudStorable;
import general.ResultState;
import general.Transformer;
import maker.ModelMakerResult;
import maker.ModelMakerStateOK;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@MockitoSettings
public class ModelCreationHandlerTest {

    @Test
    public void createModelExceptionReturnsEmpty(@Mock final Transformer<InputStream, ResultState<ModelMakerResult, ByteArrayOutputStream>> modelMaker,
                                   @Mock final CloudStorable cloudStorable,
                                   @Mock final CloudLoadable<InputStream> cloudLoadable,
                                   @Mock final CloudCopyable cloudCopyable,
                                   @Mock final InputStream inputStream,
                                   @Mock final ByteArrayOutputStream byteArrayOutputStream ) throws IOException {

        final ModelCreationHandler modelCreationHandler
                = new ModelCreationHandler(modelMaker, cloudStorable, cloudLoadable, cloudCopyable);

        when(modelMaker.transform(inputStream)).thenReturn(new ModelMakerStateOK("", byteArrayOutputStream));
        doThrow(new IOException()).when(inputStream).close();

        final Optional<ByteArrayOutputStream> apply = modelCreationHandler.createModel().apply(inputStream);

        assertThat(apply).isEmpty();
    }

}