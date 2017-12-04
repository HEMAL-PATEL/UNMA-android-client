package com.paperplanes.unma.common;

/**
 * Created by abdularis on 24/11/17.
 */

public class Optional <T> {

    private T mVal;

    public Optional() {
        mVal = null;
    }

    public Optional(T value) {
        mVal = value;
    }

    public boolean isNull() {
        return mVal == null;
    }

    public boolean isNotNull() {
        return mVal != null;
    }

    public T get() {
        return mVal;
    }

    public static <T> Optional<T> of(T value) {
        return new Optional<>(value);
    }

    public static <T> Optional<T> empty() {
        return new Optional<>();
    }
}
