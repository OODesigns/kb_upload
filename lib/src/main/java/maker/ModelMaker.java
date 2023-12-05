package maker;

import general.ResultState;
import general.Transformer;
import opennlp.tools.doccat.*;
import opennlp.tools.util.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ModelMaker implements Transformer<InputStream, ResultState<ModelMakerResult, ByteArrayOutputStream>> {

    public static final String MODEL_SUCCESSFULLY_CREATED = "Model Successfully created";

    @Override
    public ResultState<ModelMakerResult, ByteArrayOutputStream> transform(final InputStream input) {

        final TrainingParameters params = TrainingParameters.defaultParams();
        params.put(TrainingParameters.CUTOFF_PARAM, "0");

        try (final PlainTextByLineStream stream =  new PlainTextByLineStream(()->input, StandardCharsets.UTF_8);
             final ObjectStream<DocumentSample> documentSampleObjectStream = new DocumentSampleStream(stream)) {

            final DoccatModel trained = DocumentCategorizerME.train("en", documentSampleObjectStream, params, new DoccatFactory());

            final ByteArrayOutputStream modelOut = new ByteArrayOutputStream();

            trained.serialize(modelOut);

            return new ModelMakerStateOK(MODEL_SUCCESSFULLY_CREATED, modelOut);

        } catch (final IOException e) {

            return new ModelMakerStateError(e.getMessage());
        }
    }
}
