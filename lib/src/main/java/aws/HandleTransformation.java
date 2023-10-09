package aws;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import kb_upload.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;


public class HandleTransformation implements RequestHandler<Map<String, String>, Void> {

    private static final String UTTERANCE = "utterance";
    private static final String KNOWLEDGE_JSON = "knowledge.json";
    public static final String REGION_IS_MISSING = "Region is missing";
    private static final String UPLOAD_BUCKET_NAME = "Upload-BucketName";
    private static final String BUCKET_NAME_FOR_UPLOADS_IS_MISSING = "Bucket name for uploads is missing";

    private static final String UNABLE_TO_LOAD_FILE = "Unable to load file from bucket: %s and key: %s";
    public static final String UNABLE_TO_TRANSFORM_DATA = "Unable to transform data";
    private final Retrievable<S3Object, Optional<String>> fileLoader;
    private final Transformer<JSON, Optional<List<String>>> jsonTransformer;




//    private final  Retrievable<Storable<S3Object, String, Optional<S3FileSaverState>>, Region>  fileSaver;

    //Used for testing purposes only
    HandleTransformation(final Retrievable<S3Object, Optional<String>> fileLoader,
                         final Transformer<JSON, Optional<List<String>>> jsonTransformer) {
        this.fileLoader = fileLoader;
        this.jsonTransformer = jsonTransformer;
    }

    public HandleTransformation() {
//        this.fileSaver = new S3FileSaver(()->S3Client.builder().region(region).build());
        this.fileLoader = new S3FileLoader(AmazonS3ClientBuilder::defaultClient);
        this.jsonTransformer = new JSonArrayToList(UTTERANCE);
    }


//        new JSonArrayToList(UTTERANCE)
//                .transform(new S3FileLoader(this::getClient, KNOWLEDGE_JSON).toString())
//                .ifPresent(this::saveToFile);
//

    private void saveToFile(final List<String> transformedData) {

    }

    @Override
    public Void handleRequest(final Map<String, String> input, final Context context) {
        getUpBucketNameForUploads(input, context)
                .map(getS3Object())
                .flatMap(s->this.getData(s, context))
                .flatMap(transformData(context));

        return null;
    }

    private Function<JSON, Optional<String>> transformData(final Context context) {
            return json -> jsonTransformer.transform(json)
                           .orElseThrow(()->throwUnableToTransform(context))
                           .toString().describeConstable();
    }

    private TransformationException throwUnableToTransform(final Context context) {
        return new TransformationException(context, UNABLE_TO_TRANSFORM_DATA);
    }

    private Optional<JSON> getData(final S3Object s3Object, final Context context) {
        return fileLoader.retrieve(s3Object)
                    .orElseThrow(() -> throwUnableToLoadFile(context, s3Object))
                    .describeConstable()
                    .map(JSONData::new);
    }

    private TransformationException throwUnableToLoadFile(final Context context, final S3Object s3Object) {
        return new TransformationException(context, String.format(UNABLE_TO_LOAD_FILE,
                    s3Object.getBucketName(), s3Object.getKeyName()));
    }

    private Function<BucketNameProvider, S3Object> getS3Object() {
        return bucketName -> new S3Object() {

            @Override
            public String getBucketName() {
                return bucketName.get();
            }

            @Override
            public String getKeyName() {
                return KNOWLEDGE_JSON;
            }
        };
    }

    private Optional<BucketNameProvider> getUpBucketNameForUploads(final Map<String, String> input, final Context context) {
        try {
            return Optional.of(input).map(i-> new BucketName(i.get(UPLOAD_BUCKET_NAME)));
        } catch (final InvalidBucketNameException | NullPointerException e) {
            throw new TransformationException(context, BUCKET_NAME_FOR_UPLOADS_IS_MISSING);
        }
    }

/*
    private S3Client getClient() {
        return null;
    }

    @Override
    public Void handleRequest(final Map<String, String> input, final Context context) {
        getS3Object(input, context)
                .flatMap(getFileData())


        fileLoader.retrieve(s3Object).
                ifPresentOrElse(f->validateData(f, context),
                        ()->throwUnableToLoadFile(context, s3Object));



                .ifPresentOrElse(r->transformData(r, context),
                        ()->throwRegionMissing(context));





        new S3FileSaver(()->S3Client.builder().region(region).build());
        return null;
    }

    private Function<S3Object, Optional<String>> getFileData() {
        return s3Object -> {
                return fileLoader.retrieve(s3Object).or

        }
    }

    private void throwUnableToLoadFile(final Context context) {
        throw new TransformationException(context, REGION_IS_MISSING);
    }

    private void transformData(final String fileData, final Context context) {

    }


    private Optional<BucketNameProvider> getBucketName(final Map<String, String> input, final Context context) {
        try {
            return Optional.ofNullable(input.get("BucketName"))
                    .map(BucketName::new);
        } catch (final InvalidBucketNameException e) {
            throw new TransformationException(context, REGION_IS_MISSING);
        }

    }

    private void throwRegionMissing(final Context context) {
        throw new TransformationException(context, REGION_IS_MISSING);
    }

//    private void transformData(final Region region, final Context context) {
//        fileLoader.retrieve(new S3Object(new BucketName("knowledge-base-utterances"), new KeyName(KNOWLEDGE_JSON)))
//                .ifPresentOrElse(f->transformData(f, context),
//                        ()->throwUnableToLoadFile(context));
//
//    }


    private Optional<Region> getRegion(final Map<String, String> regionName, final Context context) {
        try {
            return Optional.ofNullable(regionName.get("Region"))
                    .map(Region::of);
        } catch (final IllegalArgumentException e) {
            throw new TransformationException(context, REGION_IS_MISSING);
        }
    }

 */
}
