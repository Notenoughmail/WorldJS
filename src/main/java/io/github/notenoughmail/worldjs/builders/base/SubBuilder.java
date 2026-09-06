package io.github.notenoughmail.worldjs.builders.base;

import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.JSStringConstantTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.WorldJS;
import io.github.notenoughmail.worldjs.util.Types;
import io.github.notenoughmail.worldjs.util.Validations;
import io.github.notenoughmail.worldjs.util.event.SubBuilderEvent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A {@link dev.latvian.mods.kubejs.registry.BuilderBase builder} that is <strong>only</strong> created as a derivative
 * of another builder
 * @param <T> The type being created by the builder
 */
@ReturnsSelf
@HideFromJS
public abstract class SubBuilder<T> {

    @HideFromJS
    public static final Map<Class<?>, Set<SubBuilderMap<?, ?, ?>>> SUB_BUILDER_MAPS = new IdentityHashMap<>();

    /**
     * Creates and registers a {@link SubBuilderMap} for a sub-builder method (one which calls {@link SubBuilderMap#build(Context, ResourceLocation, Object, Consumer) SubBuilderMap#build}).
     * <p>
     * Includes a callback to set the ProbeJS display of all {@link SubBuilderType}s that are registered to the map via
     * the accumulator.
     * <p>
     * Each {@code SubBuilderMap} is expected to be associated with a single sub-builder method, a {@link SubBuilderMap#forDifferentMethod(Class, String, Consumer) method}
     * is available to re-use the registered {@code SubBuilderType}s with a different class/method, and optionally with
     * different method parameters.
     * <p>
     * It is encouraged to register all {@code SubBuilderMap}s somewhere that will be called before world loading
     * happens. As part of static init of your main mod or event handler class is recommended.
     * @param classWithMethod The class the method is in
     * @param methodName The name of the method
     * @param methodOverride A consumer by which method params can be given to ProbeJS's {@link moe.wolfgirl.probejs.typescript.document.builders.MethodBuilder MethodBuilder}
     *                       without requiring a hard dependency. Has methods specifically for the sub builder type id
     *                       and consumer/callback
     * @param builderTypeAccumulator A factory for an {@link Event} to register {@code SubBuilderType}s for this method
     * @param typeName The name used to describe the sub builder types collectively in the error message created when
     *                 scriptors pass an invalid id
     * @return A {@code SubBuilderMap} containing all the {@code SubBuilderType}s (via {@code builderTypeAccucumulator})
     * that are handled by the method to {@link SubBuilderMap#build(Context, ResourceLocation, Object, Consumer) build}
     * a {@link T}
     * @param <C> The type of the {@code SubBuilder}'s constructor param
     * @param <B> The base {@code SubBuilder} type
     * @param <T> The type {@link SubBuilder#create() created} by the {@code SubBuilder}s
     * @param <E> The event type used to collect the builder types
     */
    @HideFromJS
    public static <C, B extends SubBuilder<? extends T>, T, E extends SubBuilderEvent<C, B>> SubBuilderMap<C, B, T> registerBuilders(
            Class<?> classWithMethod,
            String methodName,
            Consumer<ProbeMethodBuilder> methodOverride,
            Function<BiConsumer<ResourceLocation, SubBuilderType<C, ? extends B>>, E> builderTypeAccumulator,
            String typeName
    ) {
        final SubBuilderMap<C, B, T> map = new SubBuilderMap<>(
                WorldJS.eventMap(builderTypeAccumulator),
                methodName, methodOverride, typeName
        );
        map.assignTo(classWithMethod);
        return map;
    }

    /**
     * Creates and modifies {@link T T objects} based on the dispatched {@link SubBuilderType} and script-given callback
     * @param typeId The script-given {@link  SubBuilder} type
     * @param constructorArg The builderConstructor arg of the {@code SubBuilder}
     * @param builder The script-given consumer to be applied to the {@code SubBuilder}
     * @param typeName The name of the builder types to display in error messages
     * @return The {@link #create() built} object
     * @param <C> The type required to create a {@code SubBuilder}
     * @param <B> The base {@code SubBuilder} type
     * @param <T> The type {@link #create() created} by the {@code SubBuilder}s
     */
    public static <C, B extends SubBuilder<? extends T>, T> T build(
            Context ctx,
            ResourceLocation typeId,
            C constructorArg,
            Consumer<B> builder,
            Supplier<Map<ResourceLocation, SubBuilderType<C, ? extends B>>> types,
            String typeName
    ) {
        final SourceLine line = SourceLine.of(ctx);
        final SubBuilderType<C, ? extends B> builderInfo = types.get().get(typeId);
        if (builderInfo == null)
            throw Validations.exception(line, "Unknown " + typeName + " type: '" + typeId + "'");
        final B b = builderInfo.builderConstructor().apply(constructorArg);
        b.sourceLine = line;
        builder.accept(b);
        return b.create();
    }

    protected SourceLine sourceLine;

    protected abstract T create();

    @HideFromJS
    public record SubBuilderType<C, B>(
            TypeInfo type, // B
            Function<C, B> builderConstructor
    ) {}

    @HideFromJS
    public record SubBuilderMap<C, B extends SubBuilder<? extends T>, T>(
            @ApiStatus.Internal
            Supplier<Map<ResourceLocation, SubBuilderType<C, ? extends B>>> types,
            String methodName,
            @ApiStatus.Internal
            Consumer<ProbeMethodBuilder> methodBuilder,
            String typeName
    ) implements Iterable<Map.Entry<ResourceLocation, SubBuilderType<C, ? extends B>>> {

        /**
         * Creates and modifies {@link T T objects} based on the dispatched {@link SubBuilderType} and script given
         * callback
         * @param typeId The script-given id of a {@link SubBuilderEvent registered} {@code SubBuilderType}
         * @param constructorArg The argument to be passed to the constructor of the {@link SubBuilder}s
         * @param builder The script-given callback for setting properties of the sub-builder
         * @return The {@link SubBuilder#create() built} {@link T object}
         */
        public T build(
                Context ctx,
                ResourceLocation typeId,
                C constructorArg,
                Consumer<B> builder
        ) {
            final SourceLine line = SourceLine.of(ctx);
            final SubBuilderType<C, ? extends B> builderInfo = types().get().get(typeId);
            if (builderInfo == null)
                throw Validations.exception(line, "Unknown " + typeName() + " type: '" + typeId + "'");
            final B b = builderInfo.builderConstructor().apply(constructorArg);
            b.sourceLine = line;
            builder.accept(b);
            return b.create();
        }

        @NotNull
        @Override
        public Iterator<Map.Entry<ResourceLocation, SubBuilderType<C, ? extends B>>> iterator() {
            return types().get().entrySet().iterator();
        }

        /**
         * Creates a new {@link SubBuilderMap} that has the same {@link #types} and {@link #typeName} for a different
         * sub-builder method.
         * @param classWithMethod The class the new method is in
         * @param methodName The name of the new method
         * @param methodOverride A consumer by which method params can be given to ProbeJS's {@link moe.wolfgirl.probejs.typescript.document.builders.MethodBuilder MethodBuilder}
         *                       without requiring a hard dependency. Has methods specifically for the sub builder type
         *                       id and consumer/callback. May be {@code null} to reuse the method params of the
         *                       existing map
         */
        public SubBuilderMap<C, B, T> forDifferentMethod(
                Class<?> classWithMethod,
                String methodName,
                @Nullable Consumer<ProbeMethodBuilder> methodOverride
        ) {
            final SubBuilderMap<C, B, T> map = new SubBuilderMap<>(
                    types(),
                    methodName,
                    methodOverride == null ?
                            methodBuilder() :
                            methodOverride,
                    typeName()
            );
            map.assignTo(classWithMethod);
            return map;
        }

        private void assignTo(Class<?> clazz) {
            synchronized (SUB_BUILDER_MAPS) {
                SUB_BUILDER_MAPS.computeIfAbsent(clazz, $ -> new HashSet<>()).add(this);
            }
        }
    }

    @HideFromJS
    public interface ProbeMethodBuilder {

        void param(String paramName, TypeInfo paramType);

        default void param(String paramName, Class<?> paramType) {
            param(paramName, TypeInfo.of(paramType));
        }

        default void idParam(String paramName) {
            param(paramName, new JSStringConstantTypeInfo(id()));
        }

        default void consumerParam(String paramName) {
            param(paramName, Types.consumer(type()));
        }

        String id();

        TypeInfo type();
    }
}

