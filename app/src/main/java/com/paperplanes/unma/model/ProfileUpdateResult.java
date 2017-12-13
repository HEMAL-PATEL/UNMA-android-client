package com.paperplanes.unma.model;

import com.paperplanes.unma.model.LoginResult;

/**
 * Created by abdularis on 13/12/17.
 *
 * Updating profile info essentially re-authenticate yourself in the server side
 * So the response should be the same as login (auth)
 *
 * Subclass LoginResult for semantics
 */

public class ProfileUpdateResult extends LoginResult {
}
