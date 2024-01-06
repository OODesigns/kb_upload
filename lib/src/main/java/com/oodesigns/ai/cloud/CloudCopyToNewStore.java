package com.oodesigns.ai.cloud;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CloudCopyToNewStore implements CloudCopyable{
    public static final String UNABLE_TO_COPY_OBJECT = "Unable to copy object %s";
    private final CloudLoad<ByteArrayOutputStream> fromLoadable;
    private final CloudStorable toStorable;

    public CloudCopyToNewStore(final CloudLoad<ByteArrayOutputStream> fromLoadable, final CloudStorable toStorable) {
        this.fromLoadable = fromLoadable;
        this.toStorable = toStorable;
    }

    @Override
    public CloudStoreResult copy(final CloudObjectReference input, final CloudObjectReference output){
        return fromLoadable.retrieve(input, this::convertInputStreamToByteArrayOutputStream)
                .map(s->toStorable.store(output, s))
                .orElseThrow(()->new CloudException (String.format(UNABLE_TO_COPY_OBJECT,input)));
    }

    public ByteArrayOutputStream convertInputStreamToByteArrayOutputStream(final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        return byteArrayOutputStream;
    }
}
