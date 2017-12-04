package com.paperplanes.unma.common.exceptions;

import java.io.IOException;

/**
 * Created by abdularis on 21/11/17.
 */

public class UnauthorizedNetworkException extends IOException {
    @Override
    public String getMessage() {
        return "Unauthorized Access to Server";
    }
}
