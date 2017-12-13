package com.paperplanes.unma.common.exceptions;

/**
 * Created by abdularis on 13/12/17.
 */

public class HandledRuntimeError extends RuntimeException {
    public HandledRuntimeError(String message) {
        super(message);
    }
}
