package io.github.notenoughmail.worldjs.util;

import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.NBTWrapper;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RandomizedIntStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RotatedBlockProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.heightproviders.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface Wrappers {

    TypeInfo BLOCK_STATE_PROVIDER = TypeInfo.of(BlockStateProvider.class);
    TypeInfo WEIGHTED_BLOCK_STATE_LIST = WeightedValue.listType(TypeInfo.of(BlockState.class));
    TypeInfo BLOCK = TypeInfo.of(Block.class);
    TypeInfo BLOCK_STATE = TypeInfo.of(BlockState.class);
    TypeInfo INT_PROVIDER = TypeInfo.of(IntProvider.class);
    TypeInfo VERTICAL_ANCHOR = TypeInfo.of(VerticalAnchor.class);
    TypeInfo WEIGHTED_HEIGHT_PROVIDER_LIST = WeightedValue.listType(TypeInfo.of(HeightProvider.class));

    static BlockStateProvider blockStateProvider(Context ctx, Object o, TypeInfo target) {
        while (o instanceof Wrapper w) {
            o = w.unwrap();
        }

        return switch (o) {
            case BlockStateProvider p -> p;
            case Block b -> BlockStateProvider.simple(b);
            case BlockState s -> BlockStateProvider.simple(s);
            case List<?> l -> {
                final List<WeightedValue<BlockState>> values = Cast.to(ctx.jsToJava(l, WEIGHTED_BLOCK_STATE_LIST));
                yield new WeightedStateProvider(WeightedValue.toVanilla(values));
            }
            case Map<?, ?> m -> {
                if (!m.containsKey("type")) {
                    if (m.containsKey("simple")) {
                        final BlockState state = Cast.to(ctx.jsToJava(m.get("simple"), BLOCK_STATE));
                        yield BlockStateProvider.simple(state);
                    }
                    if (m.containsKey("block")) {
                        final BlockState state = Cast.to(ctx.jsToJava(m.get("block"), BLOCK_STATE));
                        yield BlockStateProvider.simple(state);
                    }
                    if (m.containsKey("rotate")) {
                        yield new RotatedBlockProvider(Cast.to(ctx.jsToJava(m.get("rotate"), BLOCK)));
                    }
                    if (m.containsKey("property") && m.containsKey("values") && m.containsKey("source")) {
                        final String prop = Cast.to(ctx.jsToJava(m.get("property"), TypeInfo.STRING));
                        final IntProvider vals = Cast.to(ctx.jsToJava(m.get("values"), INT_PROVIDER));
                        final BlockStateProvider prov = Cast.to(ctx.jsToJava(m.get("source"), BLOCK_STATE_PROVIDER));
                        yield new RandomizedIntStateProvider(prov, prop, vals);
                    }
                    if (m.containsKey("weighted")) {
                        final List<WeightedValue<BlockState>> values = Cast.to(ctx.jsToJava(m.get("weighted"), WEIGHTED_BLOCK_STATE_LIST));
                        yield new WeightedStateProvider(WeightedValue.toVanilla(values));
                    }
                }
                yield BlockStateProvider.CODEC.parse(RegistryAccessContainer.of(ctx).nbt(), NBTWrapper.wrapCompound(ctx, m))
                        .mapError(e -> "Failed to decode BlockStateProvider from %s: %s".formatted(m, e))
                        .getOrThrow(e -> new KubeRuntimeException(e).source(SourceLine.of(ctx)));
            }
            case null, default -> {
                try {
                    yield BlockStateProvider.simple(Cast.<BlockState>to(ctx.jsToJava(o, BLOCK_STATE)));
                } catch (Throwable ignored) {}
                yield err(ctx, o, target);
            }
        };
    }

    static VerticalAnchor verticalAnchor(Context ctx, Object o, TypeInfo target) {
        while (o instanceof Wrapper w) {
            o = w.unwrap();
        }

        return switch (o) {
            case VerticalAnchor a -> a;
            case Number n -> {
                final int y = n.intValue();
                if (y < DimensionType.MIN_Y || y > DimensionType.MAX_Y) {
                    throw new KubeRuntimeException("%s is an invalid y-value for a vertical anchor!".formatted(y))
                            .source(SourceLine.of(ctx));
                }
                yield VerticalAnchor.absolute(y);
            }
            case String s -> switch (s.toLowerCase(Locale.ROOT)) {
                case "bottom" -> VerticalAnchor.bottom();
                case "top" -> VerticalAnchor.top();
                case "-", "zero" -> VerticalAnchor.absolute(0);
                default -> throw new KubeRuntimeException("Cannot parse '%s' as a VerticalAnchor".formatted(s)).source(SourceLine.of(ctx));
            };
            case Map<?, ?> m -> {
                if (!m.containsKey("type")) {
                    if (m.containsKey("absolute")) {
                        yield verticalAnchor(ctx, m.get("absolute"), target);
                    }
                    if (m.containsKey("above_bottom")) {
                        final int val = (int) ctx.jsToJava(m.get("above_bottom"), TypeInfo.PRIMITIVE_INT);
                        if (val < DimensionType.MIN_Y || val > DimensionType.MAX_Y) {
                            throw new KubeRuntimeException("%s is an invalid offset for a vertical anchor!")
                                    .source(SourceLine.of(ctx));
                        }
                        yield VerticalAnchor.aboveBottom(val);
                    }
                    if (m.containsKey("below_top")) {
                        final int val = (int) ctx.jsToJava(m.get("below_top"), TypeInfo.PRIMITIVE_INT);
                        if (val < DimensionType.MIN_Y || val > DimensionType.MAX_Y) {
                            throw new KubeRuntimeException("%s is an invalid offset for a vertical anchor!")
                                    .source(SourceLine.of(ctx));
                        }
                        yield VerticalAnchor.belowTop(val);
                    }
                }
                yield VerticalAnchor.CODEC.parse(RegistryAccessContainer.of(ctx).nbt(), NBTWrapper.wrapCompound(ctx, m))
                        .mapError(e -> "Failed to decode VerticalAnchor from %s: %s".formatted(m, e))
                        .getOrThrow(e -> new KubeRuntimeException(e).source(SourceLine.of(ctx)));
            }
            case null, default -> err(ctx, o, target);
        };
    }

    static HeightProvider heightProvider(Context ctx, Object o, TypeInfo target) {
        while (o instanceof Wrapper w) {
            o = w.unwrap();
        }

        return switch (o) {
            case HeightProvider p -> p;
            case VerticalAnchor a -> ConstantHeight.of(a);
            case Number n -> ConstantHeight.of(verticalAnchor(ctx, n, target)); // Do the bounds check
            case List<?> l -> {
                final List<WeightedValue<HeightProvider>> values = Cast.to(ctx.jsToJava(l, WEIGHTED_HEIGHT_PROVIDER_LIST));
                yield new WeightedListHeight(WeightedValue.toVanilla(values));
            }
            case Map<?, ?> m -> {
                if (!m.containsKey("type")) {
                    if (m.containsKey("uniform")) {
                        final Map<?, ?> t = Cast.to(ctx.jsToJava(m.get("uniform"), TypeInfo.RAW_MAP));
                        yield UniformHeight.of(
                                verticalAnchor(ctx, t.get("min"), VERTICAL_ANCHOR),
                                verticalAnchor(ctx, t.get("max"), VERTICAL_ANCHOR)
                        );
                    }
                    if (m.containsKey("constant")) {
                        yield ConstantHeight.of(
                                verticalAnchor(ctx, m.get("constant"), VERTICAL_ANCHOR)
                        );
                    }
                    if (m.containsKey("trapezoid")) {
                        final Map<?, ?> t = Cast.to(ctx.jsToJava(m.get("trapezoid"), TypeInfo.RAW_MAP));
                        yield TrapezoidHeight.of(
                                verticalAnchor(ctx, t.get("min"), VERTICAL_ANCHOR),
                                verticalAnchor(ctx, t.get("max"), VERTICAL_ANCHOR),
                                t.containsKey("plateau") ?
                                        (int) ctx.jsToJava(t.get("plateau"), TypeInfo.PRIMITIVE_INT) :
                                        0
                        );
                    }
                    if (m.containsKey("weighted")) {
                        final List<WeightedValue<HeightProvider>> values = Cast.to(ctx.jsToJava(m.get("weighted"), WEIGHTED_HEIGHT_PROVIDER_LIST));
                        yield new WeightedListHeight(WeightedValue.toVanilla(values));
                    }
                    if (m.containsKey("biased")) {
                        final Map<?, ?> t = Cast.to(ctx.jsToJava(m.get("biased"), TypeInfo.RAW_MAP));
                        final VerticalAnchor min = verticalAnchor(ctx, t.get("min"), VERTICAL_ANCHOR);
                        final VerticalAnchor max = verticalAnchor(ctx, t.get("max"), VERTICAL_ANCHOR);
                        final int inner = t.containsKey("inner") ? (int) ctx.jsToJava(t.get("inner"), TypeInfo.PRIMITIVE_INT) : 1;
                        final boolean extreme = t.containsKey("extreme") && (boolean) ctx.jsToJava(t.get("extreme"), TypeInfo.PRIMITIVE_BOOLEAN);
                        yield extreme ?
                                VeryBiasedToBottomHeight.of(min, max, inner) :
                                BiasedToBottomHeight.of(min, max, inner);
                    }
                }
                yield HeightProvider.CODEC.parse(RegistryAccessContainer.of(ctx).nbt(), NBTWrapper.wrapCompound(ctx, m))
                        .mapError(e -> "Failed to decode HeightProvider from %s: %s".formatted(m, e))
                        .getOrThrow(e -> new KubeRuntimeException(e).source(SourceLine.of(ctx)));
            }
            case null, default -> err(ctx, o, target);
        };
    }

    private static <T> T err(Context ctx, Object o, TypeInfo target) {
        return Cast.to(ctx.reportConversionError(o, target));
    }

    record TargetBlockState(RuleTest target, BlockState state) {
        public OreConfiguration.TargetBlockState convert() {
            return OreConfiguration.target(target, state);
        }
    }
}
