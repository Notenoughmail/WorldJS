package io.github.notenoughmail.worldjs.util;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.holder.HolderWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.NBTWrapper;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.CubicSpline;
import net.minecraft.util.ToFloatFunction;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.SurfaceRules;
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
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.material.Fluid;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static io.github.notenoughmail.worldjs.util.Types.*;

public interface Wrappers {

    static BlockStateProvider blockStateProvider(Context ctx, Object o, TypeInfo target) {
        o = Wrapper.unwrapped(o);

        return switch (o) {
            case BlockStateProvider p -> p;
            case Block b -> BlockStateProvider.simple(b);
            case BlockState s -> BlockStateProvider.simple(s);
            case List<?> l -> {
                final List<WeightedValue<BlockState>> values = Cast.to(ctx.jsToJava(l, WEIGHTED_BLOCK_STATE_LIST));
                yield new WeightedStateProvider(WeightedValue.toVanilla(values));
            }
            case Map<?, ?> m -> {
                if (!key(m, "type")) {
                    if (key(m, "simple")) {
                        yield BlockStateProvider.simple(blockState(ctx, m.get("simple")));
                    }
                    if (key(m, "block")) {
                        yield BlockStateProvider.simple(blockState(ctx, m.get("block")));
                    }
                    if (key(m, "rotate")) {
                        yield new RotatedBlockProvider(Cast.to(ctx.jsToJava(m.get("rotate"), BLOCK)));
                    }
                    if (key(m, "randomized_int")) {
                        final Map<String, ?> r = inner(m, ctx, "randomized_int");
                        final String prop = Cast.to(ctx.jsToJava(r.get("property"), STR));
                        final IntProvider vals = Cast.to(ctx.jsToJava(r.get("values"), INT_PROVIDER));
                        final BlockStateProvider prov = Cast.to(ctx.jsToJava(r.get("source"), BLOCK_STATE_PROVIDER));
                        yield new RandomizedIntStateProvider(prov, prop, vals);
                    }
                    if (key(m, "weighted")) {
                        final List<WeightedValue<BlockState>> values = Cast.to(ctx.jsToJava(m.get("weighted"), WEIGHTED_BLOCK_STATE_LIST));
                        yield new WeightedStateProvider(WeightedValue.toVanilla(values));
                    }
                }
                yield useCodec(BlockStateProvider.CODEC, m, ctx, "BlockStateProvider");
            }
            case null, default -> {
                try {
                    yield BlockStateProvider.simple(blockState(ctx, o));
                } catch (Throwable ignored) {}
                yield err(ctx, o, target);
            }
        };
    }

    static VerticalAnchor verticalAnchor(Context ctx, Object o, TypeInfo target) {
        o = Wrapper.unwrapped(o);

        return switch (o) {
            case VerticalAnchor a -> a;
            case Number n -> {
                final int y = n.intValue();
                if (y < DimensionType.MIN_Y || y > DimensionType.MAX_Y)
                    throw Validations.exception(ctx, "%s is an invalid y-value for a vertical anchor!".formatted(y));
                yield VerticalAnchor.absolute(y);
            }
            case String s -> switch (s.toLowerCase(Locale.ROOT)) {
                case "bottom" -> VerticalAnchor.bottom();
                case "top" -> VerticalAnchor.top();
                case "-", "zero" -> VerticalAnchor.absolute(0);
                default -> throw Validations.exception(ctx, "Cannot parse '%s' as a VerticalAnchor".formatted(s));
            };
            case Map<?, ?> m -> {
                if (!key(m, "type")) {
                    if (key(m, "absolute")) {
                        yield verticalAnchor(ctx, m.get("absolute"), target);
                    }
                    if (key(m, "above_bottom")) {
                        final int val = i(ctx, m.get("above_bottom"));
                        if (val < DimensionType.MIN_Y || val > DimensionType.MAX_Y)
                            throw Validations.exception(ctx, "%s is an invalid offset for a vertical anchor!".formatted(val));
                        yield VerticalAnchor.aboveBottom(val);
                    }
                    if (key(m, "below_top")) {
                        final int val = i(ctx, m.get("below_top"));
                        if (val < DimensionType.MIN_Y || val > DimensionType.MAX_Y)
                            throw Validations.exception(ctx, "%s is an invalid offset for a vertical anchor!".formatted(val));
                        yield VerticalAnchor.belowTop(val);
                    }
                }
                yield useCodec(VerticalAnchor.CODEC, m, ctx, "VerticalAnchor");
            }
            case null, default -> err(ctx, o, target);
        };
    }

    static HeightProvider heightProvider(Context ctx, Object o, TypeInfo target) {
        o = Wrapper.unwrapped(o);

        return switch (o) {
            case HeightProvider p -> p;
            case VerticalAnchor a -> ConstantHeight.of(a);
            case Number n -> ConstantHeight.of(verticalAnchor(ctx, n, VERTICAL_ANCHOR)); // Do the bounds check
            case List<?> l -> {
                final List<WeightedValue<HeightProvider>> values = Cast.to(ctx.jsToJava(l, WEIGHTED_HEIGHT_PROVIDER_LIST));
                yield new WeightedListHeight(WeightedValue.toVanilla(values));
            }
            case Map<?, ?> m -> {
                if (!key(m, "type")) {
                    if (key(m, "uniform")) {
                        final Map<String, ?> t = inner(m, ctx, "uniform");
                        yield UniformHeight.of(
                                verticalAnchor(ctx, t.get("min"), VERTICAL_ANCHOR),
                                verticalAnchor(ctx, t.get("max"), VERTICAL_ANCHOR)
                        );
                    }
                    if (key(m, "constant")) {
                        yield ConstantHeight.of(
                                verticalAnchor(ctx, m.get("constant"), VERTICAL_ANCHOR)
                        );
                    }
                    if (key(m, "trapezoid")) {
                        final Map<String, ?> t = inner(m, ctx, "trapezoid");
                        yield TrapezoidHeight.of(
                                verticalAnchor(ctx, t.get("min"), VERTICAL_ANCHOR),
                                verticalAnchor(ctx, t.get("max"), VERTICAL_ANCHOR),
                                t.containsKey("plateau") ?
                                        i(ctx, t.get("plateau")) :
                                        0
                        );
                    }
                    if (key(m, "weighted")) {
                        final List<WeightedValue<HeightProvider>> values = Cast.to(ctx.jsToJava(m.get("weighted"), WEIGHTED_HEIGHT_PROVIDER_LIST));
                        yield new WeightedListHeight(WeightedValue.toVanilla(values));
                    }
                    if (key(m, "biased")) {
                        final Map<String, ?> t = inner(m, ctx, "biased");
                        final VerticalAnchor min = verticalAnchor(ctx, t.get("min"), VERTICAL_ANCHOR);
                        final VerticalAnchor max = verticalAnchor(ctx, t.get("max"), VERTICAL_ANCHOR);
                        final int inner = t.containsKey("inner") ? i(ctx, t.get("inner")) : 1;
                        final boolean extreme = b(ctx, "extreme", t);
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
        o = Wrapper.unwrapped(o);

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
                if (!key(m, "type")) {
                    if (key(m, "not")) {
                        yield BlockPredicate.not(
                                blockPredicate(ctx, m.get("not"), target)
                        );
                    } else if (key(m, "all")) {
                        yield BlockPredicate.allOf(Cast.<List<BlockPredicate>>to(
                                ctx.jsToJava(m.get("all"), LIST_BLOCK_PREDICATE)
                        ));
                    } else if (key(m, "any")) {
                        yield BlockPredicate.anyOf(Cast.<List<BlockPredicate>>to(
                                ctx.jsToJava(m.get("any"), LIST_BLOCK_PREDICATE)
                        ));
                    } else if (key(m, "blocks")) {
                        final Map<String, ?> t = inner(m, ctx, "blocks");
                        yield new MatchingBlocksPredicate(
                                offset(t, ctx),
                                Cast.to(ctx.jsToJava(t.get("match"), BLOCK_HOLDER_SET))
                        );
                    } else if (key(m, "tag")) {
                        final Map<String, ?> t = inner(m, ctx, "tag");
                        yield BlockPredicate.matchesTag(
                                offset(t, ctx),
                                Cast.to(ctx.jsToJava(t.get("match"), BLOCK_TAG))
                        );
                    } else if (key(m, "fluids")) {
                        final Map<String, ?> t = inner(m, ctx, "fluids");
                        yield new MatchingFluidsPredicate(
                                offset(t, ctx),
                                Cast.to(ctx.jsToJava(t.get("match"), FLUID_HOLDER_SET))
                        );
                    } else if (key(m, "replaceable")) {
                        yield BlockPredicate.replaceable(
                                offset(inner(m, ctx, "replaceable"), ctx)
                        );
                    } else if (key(m, "would_survive")) {
                        final Map<?, ?> t = inner(m, ctx, "would_survive");
                        yield BlockPredicate.wouldSurvive(
                                blockState(ctx, t.get("state")),
                                offset(t, ctx)
                        );
                    } else if (key(m, "has_sturdy_face")) {
                        final Map<?, ?> t = inner(m, ctx, "has_sturdy_face");
                        yield BlockPredicate.hasSturdyFace(
                                offset(t, ctx),
                                e(ctx, t.get("direction"), Direction.class)
                        );
                    } else if (key(m, "solid")) {
                        yield BlockPredicate.solid(
                                offset(inner(m, ctx, "solid"), ctx)
                        );
                    } else if (key(m, "no_fluid")) {
                        yield BlockPredicate.noFluid(
                                offset(inner(m, ctx, "no_fluid"), ctx)
                        );
                    } else if (key(m, "inside_world")) {
                        yield BlockPredicate.insideWorld(
                                offset(inner(m, ctx, "inside_world"), ctx)
                        );
                    } else if (key(m, "unobstructed")) {
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

    static SurfaceRules.RuleSource ruleSource(Context ctx, Object o, TypeInfo target) {
        o = Wrapper.unwrapped(o);

        return switch (o) {
            case String str when str.toLowerCase(Locale.ROOT).equals("badlands") -> SurfaceRules.bandlands();
            case List<?> l -> SurfaceRules.sequence(
                    l.stream()
                            .map(c -> ruleSource(ctx, c, target))
                            .toArray(SurfaceRules.RuleSource[]::new)
            );
            case Map<?, ?> m -> {
                if (!key(m, "type")) {
                    if (key(m, "badlands")) {
                        yield SurfaceRules.bandlands();
                    } else if (key(m, "sequence")) {
                        yield ruleSource(ctx, ListJS.of(m.get("sequence")), target);
                    } else if (key(m, "block")) {
                        yield SurfaceRules.state(blockState(ctx, m.get("block")));
                    } else if (key(m , "condition")) {
                        yield SurfaceRules.ifTrue(
                                conditionSource(ctx, m.get("if_true"), CONDITION_SOURCE),
                                ruleSource(ctx, m.get("then_run"), target)
                        );
                    }
                }
                yield useCodec(SurfaceRules.RuleSource.CODEC, m, ctx, "SurfaceRules$RuleSource");
            }
            case null -> err(ctx, o, target);
            default -> SurfaceRules.state(blockState(ctx, o));
        };
    }

    static SurfaceRules.ConditionSource conditionSource(Context ctx, Object o, TypeInfo target) {
        o = Wrapper.unwrapped(o);

        return switch (o) {
            case String str -> switch (str.toLowerCase(Locale.ROOT)) {
                case "above_preliminary_surface" -> SurfaceRules.abovePreliminarySurface();
                case "hole" -> SurfaceRules.hole();
                case "temperature" -> SurfaceRules.temperature();
                case "steep" -> SurfaceRules.steep();
                default -> err(ctx, o, target);
            };
            case List<?> l -> SurfaceRules.isBiome(
                    Cast.to(l.stream()
                            .map(c -> ctx.jsToJava(c, BIOME_RES_KEY))
                            .<ResourceKey<Biome>>map(Cast::to)
                            .toArray(ResourceKey[]::new))
            );
            case Map<?, ?> m -> {
                if (!key(m, "type")) {
                    if (key(m, "biome")) {
                        yield conditionSource(ctx, ListJS.of(m.get("biome")), target);
                    } else if (key(m, "noise_threshold")) {
                        final Map<String, ?> inner = inner(m, ctx, "noise_threshold");
                        yield SurfaceRules.noiseCondition(
                                Cast.to(ctx.jsToJava(inner.get("noise"), NOISE_PARAMS_RES_KEY)),
                                d(ctx, inner.get("min_threshold")),
                                inner.containsKey("max_threshold") ?
                                        d(ctx, inner.get("max_threshold")) :
                                        Double.MAX_VALUE
                        );
                    } else if (key(m, "vertical_gradient")) {
                        final Map<String, ?> inner = inner(m, ctx, "vertical_gradient");
                        yield SurfaceRules.verticalGradient(
                                Cast.to(ctx.jsToJava(inner.get("random_name"), STR)),
                                verticalAnchor(ctx, inner.get("true_at_and_below"), VERTICAL_ANCHOR),
                                verticalAnchor(ctx, inner.get("false_at_and_above"), VERTICAL_ANCHOR)
                        );
                    } else if (key(m, "y_above")) {
                        final Map<String, ?> inner = inner(m, ctx, "y_above");
                        final boolean stone = b(ctx, "add_stone_depth", inner);
                        final VerticalAnchor anchor = verticalAnchor(ctx, inner.get("anchor"), VERTICAL_ANCHOR);
                        final int surfaceDepthMultiplier = Validations.assertRange(
                                i(ctx, inner.get("surface_depth_multiplier")),
                                -20, 20,
                                "surface_depth_multiplier"
                        );
                        yield stone ?
                                SurfaceRules.yStartCheck(anchor, surfaceDepthMultiplier) :
                                SurfaceRules.yBlockCheck(anchor, surfaceDepthMultiplier);
                    } else if (key(m, "water")) {
                        final Map<String, ?> inner = inner(m, ctx, "water");
                        final int offset = i(ctx, inner.get("offset"));
                        final int surfaceDepthMultiplier = Validations.assertRange(
                                i(ctx, inner.get("surface_depth_multiplier")),
                                -20, 20,
                                "surface_depth_multiplier"
                        );
                        final boolean stone = b(ctx, "add_stone_depth", inner);
                        yield stone ?
                                SurfaceRules.waterStartCheck(offset, surfaceDepthMultiplier) :
                                SurfaceRules.waterBlockCheck(offset, surfaceDepthMultiplier);
                    } else if (key(m, "temperature")) {
                        yield SurfaceRules.temperature();
                    } else if (key(m, "steep")) {
                        yield SurfaceRules.steep();
                    } else if (key(m, "not")) {
                        yield SurfaceRules.not(conditionSource(ctx, m.get("not"), target));
                    } else if (key(m, "hole")) {
                        yield SurfaceRules.hole();
                    } else if (key(m, "above_preliminary_surface")) {
                        yield SurfaceRules.abovePreliminarySurface();
                    } else if (key(m ,"stone_depth")) {
                        final Map<String, ?> inner = inner(m, ctx, "stone_depth");
                        final int offset = i(ctx, inner.get("offset"));
                        final boolean addSurfaceDepth = b(ctx, "add_surface_depth", inner);
                        final int secondaryDepthRange = i(ctx, inner.get("secondary_depth_range"));
                        final CaveSurface surface = e(ctx, inner.get("surface_type"), CaveSurface.class);
                        yield SurfaceRules.stoneDepthCheck(offset, addSurfaceDepth, secondaryDepthRange, surface);
                    }
                }
                yield useCodec(SurfaceRules.ConditionSource.CODEC, m, ctx, "SurfaceRules$ConditionSource");
            }
            case null, default -> err(ctx, o, target);
        };
    }

    static DensityFunction densityFunction(Context ctx, Object o, TypeInfo target) {
        o = Wrapper.unwrapped(o);

        return switch (o) {
            case String str -> refFunc(ctx, str);
            case ResourceLocation resLoc -> refFunc(ctx, resLoc);
            case ResourceKey<?> key when key.isFor(Registries.DENSITY_FUNCTION) -> refFunc(ctx, key);
            case Number num -> DensityFunctions.constant(num.doubleValue());
            case Map<?, ?> m -> {
                if (!key(m, "type")) {
                    if (key(m, "blend_alpha")) {
                        yield DensityFunctions.blendAlpha();
                    } else if (key(m, "blend_offset")) {
                        yield DensityFunctions.blendOffset();
                    } else if (key(m, "beardifier")) {
                        yield DensityFunctions.BeardifierMarker.INSTANCE;
                    } else if (key(m, "old_blend_noise")) {
                        final Map<String, ?> inner = inner(m, ctx, "old_blend_noise");
                        yield BlendedNoise.createUnseeded(
                                d(ctx, inner, "xz_scale", 0.001, 1000),
                                d(ctx, inner, "y_scale", 0.001, 1000),
                                d(ctx, inner, "xz_factor", 0.001, 1000),
                                d(ctx, inner, "y_factor", 0.001, 1000),
                                d(ctx, inner, "smear_scale_multiplier", 1, 8)
                        );
                    } else if (key(m, "interpolated")) {
                        yield DensityFunctions.interpolated(densityFunction(ctx, m.get("interpolated"), target));
                    } else if (key(m, "flat_cache")) {
                        yield DensityFunctions.flatCache(densityFunction(ctx, m.get("flat_cache"), target));
                    } else if (key(m, "cache_2d")) {
                        yield DensityFunctions.cache2d(densityFunction(ctx, m.get("cache_2d"), target));
                    } else if (key(m, "cache_once")) {
                        yield DensityFunctions.cacheOnce(densityFunction(ctx, m.get("cache_once"), target));
                    } else if (key(m, "cache_all_in_cell")) {
                        yield DensityFunctions.cacheAllInCell(densityFunction(ctx, m.get("cache_all_in_cell"), target));
                    } else if (key(m, "noise")) {
                        final Map<String, ?> inner = inner(m, ctx, "noise");
                        yield DensityFunctions.noise(
                                noiseParams(ctx, inner.get("noise")),
                                d(ctx, inner.get("xz_scale")),
                                d(ctx, inner.get("y_scale"))
                        );
                    } else if (key(m, "end_islands")) {
                        yield DensityFunctions.endIslands(0L); // Seed is ignored in codec
                    } else if (key(m, "werid_scaled_sampler")) {
                        final Map<String, ?> inner = inner(m, ctx, "weird_scaled_sampler");
                        yield DensityFunctions.weirdScaledSampler(
                                densityFunction(ctx, inner.get("input"), target),
                                noiseParams(ctx, inner.get("noise")),
                                e(ctx, inner.get("rarity_value_mapper"), DensityFunctions.WeirdScaledSampler.RarityValueMapper.class)
                        );
                    } else if (key(m, "shifted_noise")) {
                        final Map<String, ?> inner = inner(m, ctx, "shifted_noise");
                        yield new DensityFunctions.ShiftedNoise(
                                densityFunction(ctx, inner.get("shift_x"), target),
                                densityFunction(ctx, inner.get("shift_y"), target),
                                densityFunction(ctx, inner.get("shift_z"), target),
                                d(ctx, inner.get("xz_scale")),
                                d(ctx, inner.get("y_scale")),
                                new DensityFunction.NoiseHolder(noiseParams(ctx, inner.get("noise")))
                        );
                    } else if (key(m, "range_choice")) {
                        final Map<String, ?> inner = inner(m, ctx, "range_choice");
                        yield DensityFunctions.rangeChoice(
                                densityFunction(ctx, inner.get("input"), target),
                                d(ctx, inner, "min_inclusive", -1000000.0, 1000000.0),
                                d(ctx, inner, "max_exclusive", -1000000.0, 1000000.0),
                                densityFunction(ctx, inner.get("when_in_range"), target),
                                densityFunction(ctx, inner.get("when_out_of_range"), target)
                        );
                    } else if (key(m, "shift_a")) {
                        yield DensityFunctions.shiftA(noiseParams(ctx, m.get("shift_a")));
                    } else if (key(m, "shift_b")) {
                        yield DensityFunctions.shiftB(noiseParams(ctx, m.get("shift_b")));
                    } else if (key(m, "shift")) {
                        yield DensityFunctions.shift(noiseParams(ctx, m.get("shift")));
                    } else if (key(m, "blend_density")) {
                        yield DensityFunctions.blendDensity(densityFunction(ctx, m.get("blend_density"), target));
                    } else if (key(m, "clamp")) {
                        final Map<String, ?> inner = inner(m, ctx, "clamp");
                        yield densityFunction(ctx, inner.get("input"), target).clamp(
                                d(ctx, inner, "min_value", -1000000.0, 1000000.0),
                                d(ctx, inner, "max_value", -1000000.0, 1000000.0)
                        );
                    } else if (key(m, "abs")) {
                        yield densityFunction(ctx, m.get("abs"), target).abs();
                    } else if (key(m, "square")) {
                        yield densityFunction(ctx, m.get("square"), target).square();
                    } else if (key(m, "cube")) {
                        yield densityFunction(ctx, m.get("cube"), target).cube();
                    } else if (key(m, "half_negative")) {
                        yield densityFunction(ctx, m.get("half_negative"), target).halfNegative();
                    } else if (key(m, "quarter_negative")) {
                        yield densityFunction(ctx, m.get("quarter_negative"), target).quarterNegative();
                    } else if (key(m, "squeeze")) {
                        yield densityFunction(ctx, m.get("squeeze"), target).squeeze();
                    } else if (key(m, "add")) {
                        final Map<String, ?> inner = inner(m, ctx, "add");
                        yield DensityFunctions.add(
                                densityFunction(ctx, inner.get("first"), target),
                                densityFunction(ctx, inner.get("second"), target)
                        );
                    } else if (key(m, "mul")) {
                        final Map<String, ?> inner = inner(m, ctx, "mul");
                        yield DensityFunctions.mul(
                                densityFunction(ctx, inner.get("first"), target),
                                densityFunction(ctx, inner.get("second"), target)
                        );
                    } else if (key(m, "min")) {
                        final Map<String, ?> inner = inner(m, ctx, "min");
                        yield DensityFunctions.mul(
                                densityFunction(ctx, inner.get("first"), target),
                                densityFunction(ctx, inner.get("second"), target)
                        );
                    } else if (key(m, "max")) {
                        final Map<String, ?> inner = inner(m, ctx, "max");
                        yield DensityFunctions.max(
                                densityFunction(ctx, inner.get("first"), target),
                                densityFunction(ctx, inner.get("second"), target)
                        );
                    } else if (key(m, "spline")) {
                        yield DensityFunctions.spline(densitySpline(ctx, m.get("spline")));
                    } else if (key(m, "constant")) {
                        yield DensityFunctions.constant(d(ctx, m.get("constant")));
                    } else if (key(m, "y_clamped_gradient")) {
                        final Map<String, ?> inner = inner(m, ctx, "y_clamped_gradient");
                        yield DensityFunctions.yClampedGradient(
                                i(ctx, inner, "from_y", DimensionType.MIN_Y * 2, DimensionType.MAX_Y * 2),
                                i(ctx, inner, "to_y", DimensionType.MIN_Y * 2, DimensionType.MAX_Y * 2),
                                d(ctx, inner, "from_value", -1000000.0, 1000000.0),
                                d(ctx, inner, "to_value", -1000000.0, 1000000.0)
                        );
                    }
                }
                yield useCodec(DensityFunction.HOLDER_HELPER_CODEC, m, ctx, "DensityFunction");
            }
            case null, default -> err(ctx, o, target);
        };
    }

    private static boolean key(Map<?, ?> map, String key) {
        return map.containsKey(key);
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
                .getOrThrow(e -> Validations.exception(ctx, e));
    }

    private static BlockState blockState(Context ctx, Object o) {
        return Cast.to(ctx.jsToJava(o, BLOCK_STATE));
    }

    private static int i(Context ctx, Object o) {
        return (int) ctx.jsToJava(o, INT);
    }

    private static int i(Context ctx, Map<String, ?> m, String key, int min, int max) {
        return Validations.assertRange(i(ctx, m.get(key)), min, max, key);
    }

    private static double d(Context ctx, Object o) {
        return (double) ctx.jsToJava(o, DOUB);
    }

    private static double d(Context ctx, Map<String, ?> m, String key, double min, double max) {
        return Validations.assertRange(d(ctx, m.get(key)), min, max, key);
    }

    private static boolean b(Context ctx, Object o) {
        return (boolean) ctx.jsToJava(o, BOOL);
    }

    private static boolean b(Context ctx, String key, Map<String, ?> m) {
        return m.containsKey(key) && b(ctx, m.get(key));
    }

    private static DensityFunctions.HolderHolder refFunc(Context ctx, Object o) {
        return new DensityFunctions.HolderHolder(Cast.to(HolderWrapper.wrapRef(Cast.to(ctx), o, DENSITY_FUNCTION)));
    }

    private static Holder<NormalNoise.NoiseParameters> noiseParams(Context ctx, Object o) {
        return Cast.to(HolderWrapper.wrapRef(Cast.to(ctx), o, NOISE_PARAMS));
    }

    private static <E extends Enum<E>> E e(Context ctx, Object o, Class<E> clazz) {
        return Cast.to(ctx.jsToJava(o, TypeInfo.of(clazz)));
    }

    static CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> densitySpline(Context ctx, Object o) {
        if (o instanceof Number num) {
            return CubicSpline.constant(num.floatValue());
        }

        final Map<String, ?> splineData = Cast.to(ctx.jsToJava(o, PARSE_MAP));
        final DensityFunctions.Spline.Coordinate coordinate = new DensityFunctions.Spline.Coordinate(refFunc(ctx, splineData.get("coordinate")).function());
        final ToFloatFunction<Float> valueTransformer = splineData.containsKey("value_transformer") ?
                ToFloatFunction.createUnlimited(Cast.to(ctx.jsToJava(splineData.get("value_transformer"), FLOAT_2_FLOAT))) :
                ToFloatFunction.IDENTITY;

        final CubicSpline.Builder<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> builder = CubicSpline.builder(coordinate, valueTransformer);
        final List<?> points = ListJS.orSelf(splineData.get("points"));
        for (Object point : points) {
            parseSplinePoint(builder, point, ctx);
        }

        return builder.build();
    }

    private static void parseSplinePoint(
            CubicSpline.Builder<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> builder,
            Object o,
            Context ctx
    ) {
        final Map<String, ?> pointData = Cast.to(ctx.jsToJava(o, PARSE_MAP));
        final float location = (float) ctx.jsToJava(pointData.get("location"), TypeInfo.PRIMITIVE_FLOAT);
        final Object value = pointData.get("value");
        if (value instanceof Number num) {
            final float derivative = pointData.containsKey("derivative") ?
                    (float) ctx.jsToJava(pointData.get("derivative"), TypeInfo.PRIMITIVE_FLOAT) :
                    0F;
            builder.addPoint(location, num.floatValue(), derivative);
        } else {
            builder.addPoint(location, densitySpline(ctx, value));
        }
    }

    record TargetBlockState(RuleTest target, BlockState state) {
        public OreConfiguration.TargetBlockState convert() {
            return OreConfiguration.target(target, state);
        }
    }
}
