package io.github.notenoughmail.worldjs.util;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.CustomJavaToJsWrapper;
import dev.latvian.mods.rhino.util.HideFromJS;
import io.github.notenoughmail.worldjs.util.synmethod.MethodNamespace;
import org.jetbrains.annotations.Nullable;

@HideFromJS
public interface NamespacedScriptable<T> extends CustomJavaToJsWrapper {

    @Nullable
    MethodNamespace<T> getNamespace(String name);

    @Override
    default Scriptable convertJavaToJs(Context cx, Scriptable scope, TypeInfo staticType) {
        return new NativeJavaObject(scope, this, staticType, cx) {

            @Override
            public Object get(Context cx, String name, Scriptable start) {
                final MethodNamespace<T> namespace = getNamespace(name);
                if (namespace != null) return namespace;
                return super.get(cx, name, start);
            }
        };
    }
}
