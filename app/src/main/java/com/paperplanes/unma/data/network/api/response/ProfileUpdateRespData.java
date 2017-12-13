package com.paperplanes.unma.data.network.api.response;

import com.paperplanes.unma.data.network.api.response.AuthRespData;

/**
 * Created by abdularis on 13/12/17.
 *
 * Updating profile info essentially re-authenticate yourself in the server side
 * So the response should be the same as login (auth)
 *
 * Subclass AuthRespData for semantics
 */

public class ProfileUpdateRespData extends AuthRespData {
}
