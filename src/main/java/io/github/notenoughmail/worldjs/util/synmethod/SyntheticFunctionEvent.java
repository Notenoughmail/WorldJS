package io.github.notenoughmail.worldjs.util.synmethod;

import dev.latvian.mods.rhino.type.TypeInfo;

import java.util.ArrayList;

/**
 * An event which is used to add namespaced synthetic functions to a script-side object
 * @param <R> The primary result of calling the synthetic function. Not necessarily what is returned by the function
 *           when called in scripts
 */
public interface SyntheticFunctionEvent<R> {

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
     * Start argument and type information about a synthetic function
     */
    default Args arg(Args.Arg arg) {
        return new Args(new ArrayList<>()).arg(arg);
    }

    /**
     * Create a single, standalone argument. Useful for reusing an arg between different synthetic functions
     */
    default Args.Arg singleArg(String name, TypeInfo type, String desc) {
        return new Args.Arg(name, type, desc);
    }

    /**
     * Create a single, standalone argument. Useful for reusing an arg between different synthetic functions
     */
    default Args.Arg singleArg(String name, Class<?> type, String desc) {
        return singleArg(name, TypeInfo.of(type), desc);
    }

    NamespaceRegistrar<R> namespace(String namespace);
}
