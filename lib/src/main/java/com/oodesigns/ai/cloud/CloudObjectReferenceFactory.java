package com.oodesigns.ai.cloud;

public class CloudObjectReferenceFactory {
    public static CloudObjectReference moveStore(
                                      final CloudObjectReference input,
                                      final CloudObjectReference output) {
        return new CloudObjectReference() {
            @Override
            public String getStoreName() {
                return output.getStoreName();
            }

            @Override
            public String getObjectName() {
                return input.getObjectName();
            }
        };
    }
}