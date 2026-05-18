package io.github.notenoughmail.worldjs.util;

import dev.latvian.mods.rhino.type.TypeInfo;

import java.util.ArrayList;

public interface ArgEvent {

    /**
     * Start argument and type information about a synthetic function
     */
    default Args arg(String name, TypeInfo type, String desc) {
        return new Args(new ArrayList<>()).arg(name, type, desc);
    }

    /**
     * Start argument and type information about a synthetic function
     */
    default Args arg(String name, Class<?> clazz, String desc) {
        return arg(name, TypeInfo.of(clazz), desc);
    }

    /**
     * Start arguemnt and type information about a synthetic function
     */
    default Args arg(Args.Arg arg) {
        return new Args(new ArrayList<>()).arg(arg);
    }

    /**
     * Create a single, standalone argument. Useful for reusing an arg between different functions
     */
    default Args.Arg singleArg(String name, TypeInfo type, String desc) {
        return new Args.Arg(name, type, desc);
    }

    /**
     * Create a single, standalone argument. Useful for reusing an arg between different functions
     */
    default Args.Arg singleArg(String name, Class<?> type, String desc) {
        return singleArg(name, TypeInfo.of(type), desc);
    }

}
