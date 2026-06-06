package io.github.notenoughmail.worldjs.util.synmethod;

import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.util.HideFromJS;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class MethodNamespace<R> extends BaseFunction implements NamespaceRegistrar<R> {

    @HideFromJS
    public final Map<String, Functions<R>> functions = new LinkedHashMap<>();
    public final String namespace;
    private final Consumer<R> ret;

    public MethodNamespace(String namespace, Consumer<R> ret) {
        this.namespace = namespace;
        this.ret = ret;
    }

    MethodNamespace<R> take(R accepting) {
        ret.accept(accepting);
        return this;
    }

    @Override
    public Object get(Context ctx, String name, Scriptable start) {
        final Functions<R> functions = this.functions.get(name);
        if (functions != null) {
            return functions;
        } else {
            throw Context.reportRuntimeError("Unknown function '%s.%s(...)'".formatted(namespace, name), ctx);
        }
    }

    @Override
    public NamespaceRegistrar<R> register(
            String name,
            Args arguments,
            Method<Object[], ? extends R> method,
            String functionDescription
    ) {
        final Functions<R> functions = this.functions.computeIfAbsent(name, n -> new Functions<>(n, this));
        functions.functions.put(arguments.length(), new Function<>(arguments, method, functionDescription));
        return this;
    }

    @Override
    public Set<Map.Entry<String, Functions<R>>> getAll() {
        return functions.entrySet();
    }

    public static class Functions<R> extends BaseFunction {

        private final MethodNamespace<R> parent;
        private final String name;
        @HideFromJS
        public final Int2ObjectMap<Function<R>> functions;

        public Functions(String name, MethodNamespace<R> namespace) {
            parent = namespace;
            this.name = name;
            functions = new Int2ObjectArrayMap<>();
        }

        @Override
        public Object call(Context ctx, Scriptable scope, Scriptable thisObj, Object[] args) {
            final int argLength = args.length;
            final Function<R> func = functions.get(argLength);
            if (func != null) {
                if (argLength == 0) {
                    return parent.take(func.call(args));
                }
                try {
                    return parent.take(func.call(args, ctx));
                } catch (Throwable t) {
                    throw Context.throwAsScriptRuntimeEx(t, ctx);
                }
            }
            throw Context.reportRuntimeError("No function '%s' with %s params found".formatted(name, argLength), ctx);
        }
    }

    public record Function<T>(Args args, Method<Object[], ? extends T> method, String probeDesc) {

        T call(Object[] casted) {
            return method.invoke(casted);
        }

        T call(Object[] params, Context ctx) {
            final Object[] casted = new Object[params.length];
            for (int i = 0 ; i < casted.length ; i++) {
                casted[i] = ctx.jsToJava(params[i], args.get(i).type());
            }
            return call(casted);
        }
    }
}
