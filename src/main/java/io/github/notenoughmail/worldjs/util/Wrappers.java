package io.github.notenoughmail.worldjs.util;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.NBTWrapper;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.MatchingBlocksPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.MatchingFluidsPredicate;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RandomizedIntStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RotatedBlockProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.heightproviders.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.material.Fluid;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static io.github.notenoughmail.worldjs.util.Types.*;

public interface Wrappers {

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
                        yield BlockStateProvider.simple(
                                Cast.<BlockState>to(ctx.jsToJava(m.get("simple"), BLOCK_STATE))
                        );
                    }
                    if (m.containsKey("block")) {
                        yield BlockStateProvider.simple(
                                Cast.<BlockState>to(ctx.jsToJava(m.get("block"), BLOCK_STATE))
                        );
                    }
                    if (m.containsKey("rotate")) {
                        yield new RotatedBlockProvider(Cast.to(ctx.jsToJava(m.get("rotate"), BLOCK)));
                    }
                    if (m.containsKey("randomized_int")) {
                        final Map<String, ?> r = inner(m, ctx, "randomized_int");
                        final String prop = Cast.to(ctx.jsToJava(r.get("property"), STR));
                        final IntProvider vals = Cast.to(ctx.jsToJava(r.get("values"), INT_PROVIDER));
                        final BlockStateProvider prov = Cast.to(ctx.jsToJava(r.get("source"), BLOCK_STATE_PROVIDER));
                        yield new RandomizedIntStateProvider(prov, prop, vals);
                    }
                    if (m.containsKey("weighted")) {
                        final List<WeightedValue<BlockState>> values = Cast.to(ctx.jsToJava(m.get("weighted"), WEIGHTED_BLOCK_STATE_LIST));
                        yield new WeightedStateProvider(WeightedValue.toVanilla(values));
                    }
                }
                yield useCodec(BlockStateProvider.CODEC, m, ctx, "BlockStateProvider");
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
                        final int val = (int) ctx.jsToJava(m.get("above_bottom"), INT);
                        if (val < DimensionType.MIN_Y || val > DimensionType.MAX_Y) {
                            throw new KubeRuntimeException("%s is an invalid offset for a vertical anchor!")
                                    .source(SourceLine.of(ctx));
                        }
                        yield VerticalAnchor.aboveBottom(val);
                    }
                    if (m.containsKey("below_top")) {
                        final int val = (int) ctx.jsToJava(m.get("below_top"), INT);
                        if (val < DimensionType.MIN_Y || val > DimensionType.MAX_Y) {
                            throw new KubeRuntimeException("%s is an invalid offset for a vertical anchor!")
                                    .source(SourceLine.of(ctx));
                        }
                        yield VerticalAnchor.belowTop(val);
                    }
                }
                yield useCodec(VerticalAnchor.CODEC, m, ctx, "VerticalAnchor");
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
                        final Map<String, ?> t = inner(m, ctx, "uniform");
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
                        final Map<String, ?> t = inner(m, ctx, "trapezoid");
                        yield TrapezoidHeight.of(
                                verticalAnchor(ctx, t.get("min"), VERTICAL_ANCHOR),
                                verticalAnchor(ctx, t.get("max"), VERTICAL_ANCHOR),
                                t.containsKey("plateau") ?
                                        (int) ctx.jsToJava(t.get("plateau"), INT) :
                                        0
                        );
                    }
                    if (m.containsKey("weighted")) {
                        final List<WeightedValue<HeightProvider>> values = Cast.to(ctx.jsToJava(m.get("weighted"), WEIGHTED_HEIGHT_PROVIDER_LIST));
                        yield new WeightedListHeight(WeightedValue.toVanilla(values));
                    }
                    if (m.containsKey("biased")) {
                        final Map<String, ?> t = inner(m, ctx, "biased");
                        final VerticalAnchor min = verticalAnchor(ctx, t.get("min"), VERTICAL_ANCHOR);
                        final VerticalAnchor max = verticalAnchor(ctx, t.get("max"), VERTICAL_ANCHOR);
                        final int inner = t.containsKey("inner") ? (int) ctx.jsToJava(t.get("inner"), INT) : 1;
                        final boolean extreme = t.containsKey("extreme") && (boolean) ctx.jsToJava(t.get("extreme"), BOOL);
                        yield extreme ?
                                VeryBiasedToBottomHeight.of(min, max, inner) :
                                BiasedToBottomHeight.of(min, max, inner);
                    }
                }
                yield useCodec(HeightProvider.CODEC, m, ctx, "HeightProvider");
            }
            case null, default -> err(ctx, o, target);
        };
    }

    static BlockPredicate blockPredicate(Context ctx, Object o, TypeInfo target) {
        while (o instanceof Wrapper w) {
            o = w.unwrap();
        }

        return switch (o) {
            case BlockPredicate p -> p;
            case Block b -> BlockPredicate.matchesBlocks(b);
            case Fluid f -> BlockPredicate.matchesFluids(f);
            case TagKey<?> t -> BlockPredicate.matchesTag(Cast.to(t));
            case String s -> {
                if (s.charAt(0) == '#') {
                    yield BlockPredicate.matchesTag(BlockTags.create(ResourceLocation.parse(s.substring(1))));
                } else {
                    yield BlockPredicate.matchesBlocks((Block) ctx.jsToJava(s, BLOCK));
                }
            }
            case List<?> l -> BlockPredicate.allOf(
                    l.stream()
                            .map(io -> blockPredicate(ctx, io, target))
                            .toList()
            );
            case Boolean b -> b ? BlockPredicate.alwaysTrue() : BlockPredicate.not(BlockPredicate.alwaysTrue());
            case Map<?, ?> m -> {
                if (!m.containsKey("type")) {
                    if (m.containsKey("not")) {
                        yield BlockPredicate.not(
                                blockPredicate(ctx, m.get("not"), target)
                        );
                    } else if (m.containsKey("all")) {
                        yield BlockPredicate.allOf(Cast.<List<BlockPredicate>>to(
                                ctx.jsToJava(m.get("all"), LIST_BLOCK_PREDICATE)
                        ));
                    } else if (m.containsKey("any")) {
                        yield BlockPredicate.anyOf(Cast.<List<BlockPredicate>>to(
                                ctx.jsToJava(m.get("any"), LIST_BLOCK_PREDICATE)
                        ));
                    } else if (m.containsKey("blocks")) {
                        final Map<String, ?> t = inner(m, ctx, "blocks");
                        yield new MatchingBlocksPredicate(
                                offset(t, ctx),
                                Cast.to(ctx.jsToJava(t.get("match"), BLOCK_HOLDER_SET))
                        );
                    } else if (m.containsKey("tag")) {
                        final Map<String, ?> t = inner(m, ctx, "tag");
                        yield BlockPredicate.matchesTag(
                                offset(t, ctx),
                                Cast.to(ctx.jsToJava(t.get("match"), BLOCK_TAG))
                        );
                    } else if (m.containsKey("fluids")) {
                        final Map<String, ?> t = inner(m, ctx, "fluids");
                        yield new MatchingFluidsPredicate(
                                offset(t, ctx),
                                Cast.to(ctx.jsToJava(t.get("match"), FLUID_HOLDER_SET))
                        );
                    } else if (m.containsKey("replaceable")) {
                        yield BlockPredicate.replaceable(
                                offset(inner(m, ctx, "replaceable"), ctx)
                        );
                    } else if (m.containsKey("would_survive")) {
                        final Map<?, ?> t = inner(m, ctx, "would_survive");
                        yield BlockPredicate.wouldSurvive(
                                Cast.to(ctx.jsToJava(t.get("state"), BLOCK_STATE)),
                                offset(t, ctx)
                        );
                    } else if (m.containsKey("has_sturdy_face")) {
                        final Map<?, ?> t = inner(m, ctx, "has_sturdy_face");
                        yield BlockPredicate.hasSturdyFace(
                                offset(t, ctx),
                                Cast.to(ctx.jsToJava(t.get("direction"), DIRECTION))
                        );
                    } else if (m.containsKey("solid")) {
                        yield BlockPredicate.solid(
                                offset(inner(m, ctx, "solid"), ctx)
                        );
                    } else if (m.containsKey("no_fluid")) {
                        yield BlockPredicate.noFluid(
                                offset(inner(m, ctx, "no_fluid"), ctx)
                        );
                    } else if (m.containsKey("inside_world")) {
                        yield BlockPredicate.insideWorld(
                                offset(inner(m, ctx, "inside_world"), ctx)
                        );
                    } else if (m.containsKey("unobstructed")) {
                        yield BlockPredicate.unobstructed(
                                offset(inner(m, ctx, "unobstructed"), ctx)
                        );
                    }
                }
                yield useCodec(BlockPredicate.CODEC, m, ctx, "BlockPredicate");
            }
            case null, default -> err(ctx, o, target);
        };
    }

    private static Vec3i offset(Map<?, ?> t, Context ctx) {
        return t.containsKey("offset") ? Cast.to(ctx.jsToJava(t.get("offset"), VEC3I)) : Vec3i.ZERO;
    }

    private static Map<String, ?> inner(Map<?, ?> m, Context ctx, String name) {
        return Cast.to(ctx.jsToJava(m.get(name), PARSE_MAP));
    }

    private static <T> T err(Context ctx, Object o, TypeInfo target) {
        return Cast.to(ctx.reportConversionError(o, target));
    }

    private static <T> T useCodec(Codec<T> codec, Map<?, ?> m, Context ctx, String type) {
        return codec.parse(RegistryAccessContainer.of(ctx).nbt(), NBTWrapper.wrapCompound(ctx, m))
                .mapError(e -> "Failed to decode %s from %s: %s".formatted(type, m, e))
                .getOrThrow(e -> new KubeRuntimeException(e).source(SourceLine.of(ctx)));
    }

    record TargetBlockState(RuleTest target, BlockState state) {
        public OreConfiguration.TargetBlockState convert() {
            return OreConfiguration.target(target, state);
        }
    }
}
