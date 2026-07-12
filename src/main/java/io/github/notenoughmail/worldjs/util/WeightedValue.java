package io.github.notenoughmail.worldjs.util;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.rhino.*;
import dev.latvian.mods.rhino.type.RecordTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.util.random.SimpleWeightedRandomList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * An abstracted representation of a weighted value which can be freely converted to other
 * {@link SimpleWeightedRandomList weighted representations}.
 * <p>
 * Can be created through scripts via the typical record means or as an inline {@code value} object, defaulting to a weight of 1. So for a
 * {@code List<WeightedValue<Block>>} parameter, a scriptor could pass
 * <pre><code>
 *     [
 *      // Object form, generally preferred if not inline
 *      {
 *          weight: 17,
 *          value: 'minecraft:dirt'
 *      },
 *      // Callback form
 *      w => {
 *          w.weight = 7;
 *          w.value = 'minecraft:obsidian'
 *      },
 *      // List form
 *      [
 *          2,
 *          'minecraft:stone'
 *      ],
 *      // Inline value, has a weight of 1
 *      'minecraft:sponge'
 *     ]
 * </code></pre>
 * <p>
 * A single {@code WeightedValue} or a null/undefined instance within a list is meaningless and should not occur
 */
@Info(
        value = "A weighted value",
        params = {
                @Param(name = "weight", value = "The weight of the value"),
                @Param(name = "value", value = "The value")
        }
)
public record WeightedValue<T>(int weight, T value) {

    @HideFromJS
    public static <T, E extends T> SimpleWeightedRandomList<T> toVanilla(List<WeightedValue<E>> weightedValues) {
        return switch (weightedValues.size()) {
            case 0 -> SimpleWeightedRandomList.empty();
            case 1 -> SimpleWeightedRandomList.single(weightedValues.getFirst().value);
            default -> {
                final SimpleWeightedRandomList.Builder<T> b = SimpleWeightedRandomList.builder();
                for (WeightedValue<E> w : weightedValues) {
                    b.add(w.value(), w.weight());
                }
                yield b.build();
            }
        };
    }

    @HideFromJS
    public static TypeInfo listType(TypeInfo generic) {
        return TypeInfo.RAW_LIST.withParams(TYPE.withParams(generic));
    }

    @HideFromJS
    public static TypeInfo listType(Class<?> generic) {
        return listType(TypeInfo.of(generic));
    }


    private static final RecordTypeInfo TYPE = Cast.to(TypeInfo.of(WeightedValue.class));
    private static final TypeInfo CONSUMER_TYPE = TypeInfo.RAW_CONSUMER.withParams(TypeInfo.RAW_MAP.withParams(TypeInfo.STRING, TypeInfo.NONE));

    @HideFromJS
    public static WeightedValue<?> wrap(Context ctx, Object from, TypeInfo target) {
        return switch (from) {
            case null -> throw Context.reportRuntimeError("Can't interpret 'null' as a weighted value", ctx);
            case WeightedValue<?> w -> {
                final TypeInfo param = target.param(0);
                try {
                    yield new WeightedValue<>(w.weight(), ctx.jsToJava(w.value(), param));
                } catch (Exception e) {
                    throw Context.reportRuntimeError("Cannot convert '%s' to %s".formatted(w.value(), param.signature()), ctx);
                }
            }
            case Map<?, ?> m when m.containsKey("value") -> c(ctx, m, target);
            case Iterable<?> itr -> c(ctx, itr, target);
            case Callable c -> c(ctx, c, target);
            case Object o when Undefined.isUndefined(o) -> throw Context.reportRuntimeError("Can't interpret 'undefined' as a weighted value", ctx);
            default -> new WeightedValue<>(1, ctx.jsToJava(from, target.param(0)));
        };
    }

    private static WeightedValue<?> c(Context ctx, Object from, TypeInfo target) {
        if (from instanceof NativeArray || from instanceof NativeJavaList) {
            final Object[] arr = Cast.to(ctx.arrayOf(from, TypeInfo.NONE));
            return fromArr(arr, ctx, target);
        } else if (from instanceof Map<?,?> || from instanceof NativeJavaObject) {
            final Map<String, ?> map = Cast.to(ctx.mapOf(from, TypeInfo.STRING, TypeInfo.NONE));
            return fromMap(map, ctx, target);
        } else if (from instanceof Callable) {
            final Map<String, ?> map = new HashMap<>(2);
            final Consumer<Map<String, ?>> consumer = Cast.to(ctx.jsToJava(from, CONSUMER_TYPE));
            consumer.accept(map);
            return fromMap(map, ctx, target);
        } else {
            return Cast.to(ctx.reportConversionError(from, target));
        }
    }

    private static WeightedValue<?> fromArr(Object[] from, Context ctx, TypeInfo target) {
        final Object[] args = new Object[2];
        args[0] = 1;
        final int len = Math.min(2, from.length);
        for (int i = 0 ; i < len ; i++) {
            args[i] = ctx.jsToJava(
                    from[i],
                    i == 0 ?
                            TypeInfo.PRIMITIVE_INT :
                            target.param(0)
            );
        }
        return make(args[0], args[1], ctx);
    }

    private static WeightedValue<?> fromMap(Map<String, ?> from, Context ctx, TypeInfo target) {
        final RecordTypeInfo.Data data = TYPE.getData();
        final Object[] args = new Object[2];
        args[0] = 1;

        for (Map.Entry<String, ?> entry : from.entrySet()) {
            final RecordTypeInfo.Component c = data.componentMap().get(entry.getKey());

            if (c != null) {
                args[c.index()] = ctx.jsToJava(
                        entry.getValue(),
                        c.index() == 0 ?
                                TypeInfo.PRIMITIVE_INT :
                                target.param(0)
                );
            }
        }
        return make(args[0], args[1], ctx);
    }

    private static WeightedValue<?> make(Object weight, Object value, Context ctx) {
        final int w = (int) weight;
        if (w < 1)
            throw Context.reportRuntimeError("Weight cannot be less than 1!", ctx);
        return new WeightedValue<>(w, value);
    }
}
