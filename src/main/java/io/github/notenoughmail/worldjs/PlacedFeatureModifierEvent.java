package io.github.notenoughmail.worldjs;

import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import io.github.notenoughmail.worldjs.builders.base.PlacedFeatureBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;

import java.util.*;

public class PlacedFeatureModifierEvent extends Event {

    public static Map<String, ModifierNamespace> createNamespaces() {
        final Map<String, ModifierNamespace> m = new HashMap<>();
        NeoForge.EVENT_BUS.post(new PlacedFeatureModifierEvent(m));
        return Collections.unmodifiableMap(m);
    }

    private final Map<String, ModifierNamespace> builder;

    public PlacedFeatureModifierEvent(Map<String, ModifierNamespace> builder) {
        this.builder = builder;
    }

    /**
     * Start a new modifier namespace, where the
     */
    public ModifierNamespace namespace(String namespace) {
        return builder.computeIfAbsent(namespace, ModifierNamespace::new);
    }

    /**
     * Start argument and type information about a synthetic function
     */
    public Args arg(String name, TypeInfo type, String desc) {
        return new Args(new ArrayList<>()).arg(name, type, desc);
    }

    /**
     * Start argument and type information about a synthetic function
     */
    public Args arg(String name, Class<?> clazz, String desc) {
        return arg(name, TypeInfo.of(clazz), desc);
    }

    /**
     * Start arguemnt and type information about a synthetic function
     */
    public Args arg(Args.Arg arg) {
        return new Args(new ArrayList<>()).arg(arg);
    }

    /**
     * Create a single, standalone argument. Useful for reusing an arg between different functions
     */
    public Args.Arg singleArg(String name, TypeInfo type, String desc) {
        return new Args.Arg(name, type, desc);
    }

    /**
     * Create a single, standalone argument. Useful for reusing an arg between different functions
     */
    public Args.Arg singleArg(String name, Class<?> type, String desc) {
        return singleArg(name, TypeInfo.of(type), desc);
    }

    public record Args(List<Arg> args) {

        static final Args EMPTY = new Args(List.of());

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
         * Add a single, pre-existing argument to the arg list. See {@link #singleArg(String, TypeInfo, String)}
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

        public record Arg(String name, TypeInfo type, String desc) {}
    }

    public class ModifierNamespace extends BaseFunction {

        @HideFromJS
        public final Map<String, ModifierFunctions> functions = new HashMap<>();

        public final String namespace;

        ModifierNamespace(String name) {
            namespace = name;
        }

        /**
         * Registers a unit (no argument) placement modifier function to the namespace
         * @param name The function name to call
         * @param modifier The modifier instance
         * @param functionDescription ProbeJS information about the modifier
         */
        @HideFromJS
        public ModifierNamespace unit(String name, PlacementModifier modifier, String functionDescription) {
            return register(name, Args.EMPTY, o -> modifier, functionDescription);
        }

        /**
         * Registers a dynamic placement modifier function to the namespace
         * @param name The name of the function to call
         * @param arguments The argument information
         * @param method The modifier constructor. The given array will have the same length as args given in {@code arguments}
         *               and they will be of the specified type in the same order as the args are given
         * @param functionDescription ProbeJS information about the modifier
         */
        @HideFromJS
        public ModifierNamespace register(
                String name,
                Args arguments,
                Method<Object[]> method,
                String functionDescription
        ) {
            final ModifierFunctions functions = this.functions.computeIfAbsent(name, n -> new ModifierFunctions(n, this));
            functions.functions.put(arguments.length(), new ModifierFunction(arguments, method, functionDescription));
            return this;
        }

        /**
         * Registers a dynamic placement modifier function with a single argument to the namespace
         * @param name The name of the function to call
         * @param arg The argument information
         * @param method The modifier constructor
         * @param functionDescription ProbeJS information about the modifier
         */
        @HideFromJS
        public <T> ModifierNamespace registerSingleArg(
                String name,
                Args.Arg arg,
                Method<T> method,
                String functionDescription
        ) {
            return register(name, new Args(List.of(arg)), a -> method.invoke(Cast.to(a[0])), functionDescription);
        }

        /**
         * Register a dynamic placement modifier function with a single argument to the namespace
         * @param name The name of the function to call
         * @param argName The name of the argument
         * @param argType The {@link TypeInfo} describing the argument
         * @param argDesc ProbeJS information about the argument
         * @param method The modifier constructor
         * @param functionDescription ProbeJS information about the modifier
         */
        @HideFromJS
        public <T> ModifierNamespace registerSingleArg(
                String name,
                String argName,
                TypeInfo argType,
                String argDesc,
                Method<T> method,
                String functionDescription
        ) {
            return registerSingleArg(name, singleArg(argName, argType, argDesc), method, functionDescription);
        }

        /**
         * Register a dynamic placement modifier function with a single argument to the namespace
         * @param name The name of the function to call
         * @param argName The name of the argument
         * @param argType The argument type
         * @param argDesc ProbeJS inforamtion about the argument
         * @param method The modifier constructor
         * @param functionDescription ProbeJS information about the modifier
         */
        @HideFromJS
        public <T> ModifierNamespace registerSingleArg(
                String name,
                String argName,
                Class<T> argType,
                String argDesc,
                Method<T> method,
                String functionDescription
        ) {
            return registerSingleArg(name, argName, TypeInfo.of(argType), argDesc, method, functionDescription);
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
        public final Int2ObjectMap<ModifierFunction> functions;

        public ModifierFunctions(String name, ModifierNamespace namespace) {
            parent = namespace;
            this.name = name;
            functions = new Int2ObjectArrayMap<>();
        }

        @Override
        public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
            final int argLength = args.length;
            final ModifierFunction func = functions.get(args.length);
            if (func != null) {
                if (argLength == 0) {
                    PlacedFeatureBuilder.Modifiers.accept(func.call(args));
                    return parent;
                }
                try {
                    PlacedFeatureBuilder.Modifiers.accept(func.call(args, cx));
                    return parent;
                } catch (Throwable t) {
                    throw Context.throwAsScriptRuntimeEx(t, cx);
                }
            }
            throw Context.reportRuntimeError("No method '%s' with %s params found".formatted(name, args.length), cx);
        }
    }

    public record ModifierFunction(Args args, Method<Object[]> method, String probeDesc) {

        PlacementModifier call(Object[] params) throws IllegalArgumentException{
            return method.invoke(params);
        }

        PlacementModifier call(Object[] params, Context ctx) {
            final Object[] casted = new Object[params.length];
            for (int i = 0 ; i < casted.length ; i++) {
                casted[i] = ctx.jsToJava(params[i], args.get(i).type());
            }
            return call(casted);
        }
    }

    @FunctionalInterface
    public interface Method<PARAMS> {
        PlacementModifier invoke(PARAMS params) throws IllegalArgumentException;
    }
}
