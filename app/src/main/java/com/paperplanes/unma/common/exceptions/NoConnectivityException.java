package com.paperplanes.unma.common.exceptions;

import java.io.IOException;

/**
 * Created by abdularis on 14/11/17.
 */

public class NoConnectivityException extends IOException {
    @Override
    public String getMessage() {
        return "No Connectivity";
    }
}
