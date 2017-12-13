package com.paperplanes.unma.data;

import com.paperplanes.unma.model.Profile;

/**
 * Created by abdularis on 07/12/17.
 */

public interface ProfileStore {
    void store(Profile profile);
    Profile get();
    void clear();
}
