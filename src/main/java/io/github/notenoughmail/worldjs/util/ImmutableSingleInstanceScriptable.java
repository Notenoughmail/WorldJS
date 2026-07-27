package io.github.notenoughmail.worldjs.util;

import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.Undefined;
import dev.latvian.mods.rhino.util.DefaultValueTypeHint;
import dev.latvian.mods.rhino.util.HideFromJS;

import java.util.IdentityHashMap;
import java.util.Map;

@HideFromJS
public interface ImmutableSingleInstanceScriptable extends Scriptable {

    Map<ImmutableSingleInstanceScriptable, Scriptable> PARENT_SCOPES = new IdentityHashMap<>();

    @Override
    default Object get(Context cx, int index, Scriptable start) {
        return Undefined.INSTANCE;
    }

    @Override
    default boolean has(Context cx, int index, Scriptable start) {
        return false;
    }

    @Override
    default void put(Context cx, String name, Scriptable start, Object value) {
        throw Context.reportRuntimeError("Cannot put objects into " + this, cx);
    }

    @Override
    default void put(Context cx, int index, Scriptable start, Object value) {
        throw Context.reportRuntimeError("Cannot put objects into " + this, cx);
    }

    @Override
    default void delete(Context cx, String name) {
        throw Context.reportRuntimeError("Cannot delete objects from " + this, cx);
    }

    @Override
    default void delete(Context cx, int index) {
        throw Context.reportRuntimeError("Cannot delete objects from " + this, cx);
    }

    @Override
    default Scriptable getPrototype(Context cx) {
        throw Context.reportRuntimeError("Cannot access prototype of " + this, cx);
    }

    @Override
    default void setPrototype(Scriptable prototype) {
        throw new KubeRuntimeException("Cannot set prototype of " + this);
    }

    @Override
    default boolean hasInstance(Context cx, Scriptable instance) {
        return instance == this;
    }

    @Override
    default Object getDefaultValue(Context cx, DefaultValueTypeHint hint) {
        return switch (hint) {
            case STRING -> getClassName();
            case BOOLEAN -> true;
            case CLASS -> this;
            case NUMBER -> 0;
            case null, default -> null;
        };
    }

    @Override
    default void setParentScope(Scriptable parent) {
        PARENT_SCOPES.put(this, parent);
    }

    @Override
    default Scriptable getParentScope() {
        return PARENT_SCOPES.get(this);
    }
}
