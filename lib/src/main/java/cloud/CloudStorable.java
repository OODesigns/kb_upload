package cloud;

import general.Storable;

import java.io.ByteArrayOutputStream;

public interface CloudStorable extends Storable<CloudObjectReference, ByteArrayOutputStream, CloudSaverResult> {}
