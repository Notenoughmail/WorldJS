package io.github.notenoughmail.worldjs;

import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.EvaluatorException;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import io.github.notenoughmail.worldjs.builders.base.PlacedFeatureBuilder;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class PlacedFeatureModifierEvent extends Event {

    public static Map<String, ModifierNamespace> createNamespaces() {
        final Map<String, ModifierNamespace> m = new HashMap<>();
        NeoForge.EVENT_BUS.post(new PlacedFeatureModifierEvent(m));
        return m;
    }

    private final Map<String, ModifierNamespace> builder;

    public PlacedFeatureModifierEvent(Map<String, ModifierNamespace> builder) {
        this.builder = builder;
    }

    public ModifierNamespace namespace(String namespace) {
        return builder.computeIfAbsent(namespace, ModifierNamespace::new);
    }

    public class ModifierNamespace extends BaseFunction {

        @HideFromJS
        public final Map<String, ModifierFunctions> functions = new HashMap<>();

        public final String namespace;

        ModifierNamespace(String name) {
            namespace = name;
        }

        private static final String[] EMPTY = {};

        @HideFromJS
        public ModifierNamespace unit(String name, PlacementModifier modifier) {
            return register(name, TypeInfo.EMPTY_ARRAY, EMPTY, o -> modifier);
        }

        @HideFromJS
        public ModifierNamespace register(
                String name,
                TypeInfo[] args,
                String[] argNames,
                Method<Object[]> method
        ) {
            if (argNames.length != args.length) throw new IllegalArgumentException("Must have same number of args as arg names!");
            final ModifierFunctions functions = this.functions.computeIfAbsent(name, n -> new ModifierFunctions(n, this));
            functions.functions.add(new ModifierFunction(args, argNames, method));
            return this;
        }

        public <T> ModifierNamespace registerSingleArg(
                String name,
                TypeInfo arg,
                String argName,
                Method<T> method
        ) {
            return register(name, new TypeInfo[] { arg }, new String[] { argName }, o -> method.invoke(Cast.to(o[0])));
        }

        @HideFromJS
        public <T> ModifierNamespace registerSingleArg(
                String name,
                Class<T> arg,
                String argName,
                Method<T> method
        ) {
            return registerSingleArg(name, TypeInfo.of(arg), argName, method);
        }

        @Override
        public Object get(Context cx, String name, Scriptable start) {
            final ModifierFunctions functions = this.functions.get(name);
            if (functions != null) {
                return functions;
            } else {
                throw Context.reportRuntimeError("Unknown function '%s'".formatted(name), cx);
            }
        }
    }

    public class ModifierFunctions extends BaseFunction {

        private final ModifierNamespace parent;
        private final String name;
        @HideFromJS
        public final List<ModifierFunction> functions;

        public ModifierFunctions(String name, ModifierNamespace namespace) {
            parent = namespace;
            this.name = name;
            functions = new ArrayList<>();
        }

        @Override
        public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
            boolean functionCalled = false;
            for (ModifierFunction func : functions) {
                if (args.length == func.args().length) {
                    if (args.length == 0) {
                        final PlacementModifier mod = func.call(args);
                        functionCalled = true;
                        PlacedFeatureBuilder.Modifiers.accept(mod);
                        break;
                    } else {
                        try {
                            final Object[] casted = new Object[args.length];
                            func.cast(casted, args, cx);
                            final PlacementModifier mod = func.call(casted);
                            functionCalled = true;
                            PlacedFeatureBuilder.Modifiers.accept(mod);
                            break;
                        } catch (Throwable t) {
                            throw Context.throwAsScriptRuntimeEx(t, cx);
                        }
                    }
                }
            }
            if (!functionCalled) throw Context.reportRuntimeError("No method '%s' with %s params found".formatted(name, args.length), cx);
            return parent;
        }
    }

    public record ModifierFunction(TypeInfo[] args, String[] argNames, Method<Object[]> method) {

        void cast(Object[] ret, Object[] passed, Context ctx) throws EvaluatorException {
            for (int i = 0 ; i < ret.length ; i++) {
                ret[i] = ctx.jsToJava(passed[i], args[i]);
            }
        }

        PlacementModifier call(Object[] params) throws IllegalArgumentException{
            return method.invoke(params);
        }
    }

    @FunctionalInterface
    public interface Method<PARAMS> {
        PlacementModifier invoke(PARAMS params) throws IllegalArgumentException;
    }
}
