package io.github.notenoughmail.worldjs.util;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.Undefined;
import dev.latvian.mods.rhino.util.HideFromJS;
import io.github.notenoughmail.worldjs.util.synmethod.MethodNamespace;

import java.util.Map;

@HideFromJS
public interface NamespacedScriptable<T> extends ImmutableSingleInstanceScriptable {

    Map<String, MethodNamespace<T>> namespaces();

    @Override
    default Object[] getIds(Context cx) {
        return namespaces().keySet().toArray();
    }

    @Override
    default Object get(Context cx, String name, Scriptable start) {
        final MethodNamespace<T> namespace = namespaces().get(name);
        if (namespace == null) {
            return Undefined.INSTANCE;
        }
        return namespace;
    }

    @Override
    default boolean has(Context cx, String name, Scriptable start) {
        return namespaces().containsKey(name);
    }
}
