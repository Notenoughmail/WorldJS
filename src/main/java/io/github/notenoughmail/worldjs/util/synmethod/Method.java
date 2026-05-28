package io.github.notenoughmail.worldjs.util.synmethod;

public interface Method<PARAMS, R> {

    R invoke(PARAMS params) throws IllegalArgumentException;
}
