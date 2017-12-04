package com.paperplanes.unma.common.exceptions;

/**
 * Created by abdularis on 27/11/17.
 */

public class ExternalStorageUnavailableException extends RuntimeException {
    @Override
    public String getMessage() {
        return "External Storage Not Available";
    }
}
