package io.github.notenoughmail.worldjs.util;

import dev.latvian.mods.rhino.type.TypeInfo;
import io.github.notenoughmail.worldjs.util.event.PlacedFeatureModifierEvent;

import java.util.List;

public record Args(List<Arg> args) {

    public static final Args EMPTY = new Args(List.of());

    /**
     * Add an argument to the argument list
     */
    public Args arg(String name, TypeInfo type, String desc) {
        return arg(new Arg(name, type, desc));
    }

    /**
     * Add an argument to the argument list
     */
    public Args arg(String name, Class<?> clazz, String desc) {
        return arg(name, TypeInfo.of(clazz), desc);
    }

    /**
     * Add a single, pre-existing argument to the arg list. See {@link ArgEvent#singleArg(String, TypeInfo, String)}
     */
    public Args arg(Arg arg) {
        args.add(arg);
        return this;
    }

    public int length() {
        return args.size();
    }

    public Arg get(int i) {
        return args.get(i);
    }

    public record Arg(String name, TypeInfo type, String desc) {
    }
}
