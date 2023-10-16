package kb_upload;

import opennlp.tools.doccat.*;
import opennlp.tools.util.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class ModelMaker implements Transformer1_1<InputStream, Optional<ByteArrayOutputStream>>{
    @Override
    public Optional<ByteArrayOutputStream> transform(final InputStream input) {

        final TrainingParameters params = TrainingParameters.defaultParams();
        params.put(TrainingParameters.CUTOFF_PARAM, "0");

        try (final PlainTextByLineStream stream =  new PlainTextByLineStream(()->input, StandardCharsets.UTF_8);
             final ObjectStream<DocumentSample> documentSampleObjectStream = new DocumentSampleStream(stream)) {

            final DoccatModel trained = DocumentCategorizerME.train("en", documentSampleObjectStream, params, new DoccatFactory());

            final ByteArrayOutputStream modelOut = new ByteArrayOutputStream();

            trained.serialize(modelOut);

            return Optional.of(modelOut);

        } catch (final IOException e) {
            return Optional.empty();
        }
    }
}
