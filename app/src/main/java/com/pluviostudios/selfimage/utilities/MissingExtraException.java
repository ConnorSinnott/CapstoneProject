package com.pluviostudios.selfimage.utilities;

/**
 * Created by Spectre on 6/20/2016.
 */
public class MissingExtraException extends RuntimeException {

    public MissingExtraException(String expectedExtra) {
        super("Expected extra is missing: " + expectedExtra);
    }

}
