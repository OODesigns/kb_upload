package com.oodesigns.ai.assistant_configuration_creator;
import com.oodesigns.ai.cloud.CloudCopyable;
import com.oodesigns.ai.cloud.CloudLoadable;
import com.oodesigns.ai.cloud.CloudStorable;
import com.oodesigns.ai.general.ResultState;
import com.oodesigns.ai.general.Transformer;
import com.oodesigns.ai.maker.ModelMakerResult;
import com.oodesigns.ai.maker.ModelMakerStateOK;
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
class ModelCreationHandlerTest {

    @Test
    void createModelExceptionReturnsEmpty(@Mock final Transformer<InputStream, ResultState<ModelMakerResult, ByteArrayOutputStream>> modelMaker,
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