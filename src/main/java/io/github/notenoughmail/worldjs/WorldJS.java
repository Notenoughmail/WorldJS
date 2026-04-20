package io.github.notenoughmail.worldjs;

import com.mojang.logging.LogUtils;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.rhino.type.TypeInfo;
import io.github.notenoughmail.worldjs.util.Wrappers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.placement.*;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(WorldJS.MODID)
public class WorldJS {

    public static final String MODID = "worldjs";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WorldJS() {
        NeoForge.EVENT_BUS.addListener(this::addVanillaPlacementModifiers);
    }

    private static final TypeInfo BLOCK_PREDICATE = TypeInfo.of(BlockPredicate.class);
    private static final TypeInfo HEIGHTMAP = TypeInfo.of(Heightmap.Types.class);

    private void addVanillaPlacementModifiers(PlacedFeatureModifierEvent event) {

        final PlacedFeatureModifierEvent.Args anchorRange =
                event.arg("minInclusive", Wrappers.VERTICAL_ANCHOR, "The lower placement bound")
                        .arg("maxInclusive", Wrappers.VERTICAL_ANCHOR, "The upper placement bound");

        final PlacedFeatureModifierEvent.Args.Arg n2c, nf, hs, vs, ds, tc, ms, rth, rti, rta;

        event.namespace("minecraft")
                .unit(
                        "biome",
                        BiomeFilter.biome(),
                        "Add a 'minecraft:biome' placement filter"
                )
                .unit(
                        "inSquare",
                        InSquarePlacement.spread(),
                        "Add a 'minecraft:in_square' placement modifier"
                )
                .<IntProvider>registerSingleArg(
                        "count",
                        "count",
                        Wrappers.INT_PROVIDER,
                        "How many times the placed feature should be placed",
                        CountPlacement::of,
                        "Add a 'minecraft:count' placement modifier"
                )
                .registerSingleArg(
                        "fixed",
                        "positions",
                        BlockPos[].class,
                        "The positions to place the feature at",
                        FixedPlacement::of,
                        "Add a 'minecraft:fixed_placement' placement modifier"
                )
                .registerSingleArg(
                        "rarityFilter",
                        "chance",
                        int.class,
                        "The chance the feature will successfully place as `1/chance`",
                        i -> {
                            if (i < 1)
                                throw new IllegalArgumentException("'chance' must be positive");
                            return RarityFilter.onAverageOnceEvery(i);
                        },
                        "Add a 'minecraft:rarity_filter' placement filter"
                )
                .registerSingleArg(
                        "carvingMask",
                        "carvingStep",
                        GenerationStep.Carving.class,
                        "The carving step volume for which the feature can place",
                        CarvingMaskPlacement::forStep,
                        "Add a 'minecraft:carving_mask' placement modifier"
                )
                .registerSingleArg(
                        "heightmap",
                        "heightmap",
                        HEIGHTMAP,
                        "The heightmap to place the feature at",
                        HeightmapPlacement::onHeightmap,
                        "Add a 'minecraft:heightmap' placement modifier"
                )
                .register(
                        "noiseBasedCount",
                        event.arg(n2c = event.singleArg("noiseToCountRatio", int.class, "Ratio of noise value to count"))
                                .arg(nf = event.singleArg("noiseFactor", double.class, "Horizontal scale factor of the noise. Higher values make wider, more spaced out peaks"))
                                .arg("noiseOffset", double.class, "Vertical offset of the noise"),
                        a -> NoiseBasedCountPlacement.of(
                                i(a[0]),
                                d(a[1]),
                                d(a[2])
                        ),
                        "Add a 'minecraft:noise_based_count' placement modifier"
                )
                .register(
                        "noiseBasedCount",
                        event.arg(n2c).arg(nf),
                        a -> NoiseBasedCountPlacement.of(
                                i(a[0]),
                                d(a[1]),
                                0
                        ),
                        "Add a 'minecraft:noise_based_count' placement modifier"
                )
                .register(
                        "noiseThresholdCount",
                        event.arg("noiseLevel", double.class, "The threshold for determining if to use `belowNoise` or `aboveNoise`")
                                .arg("belowNoise", int.class, "The count used when below the threshold")
                                .arg("aboveNoise", int.class, "The count used when above the threshold"),
                        a -> NoiseThresholdCountPlacement.of(
                                d(a[0]),
                                i(a[1]),
                                i(a[2])
                        ),
                        "Add a 'minecraft:noise_threshold_count' placement modifier"
                )
                .register(
                        "randomOffset",
                        event.arg(hs = event.singleArg("xzSpread", Wrappers.INT_PROVIDER, "The horizontal spread"))
                                .arg(vs = event.singleArg("ySpread", Wrappers.INT_PROVIDER, "The vertical spread")),
                        a -> {
                            final IntProvider xz = intProvider(a[0]), y = intProvider(a[1]);
                            if (xz.getMaxValue() < -16 || y.getMinValue() < -16 || xz.getMaxValue() > 16 || y.getMaxValue() > 16)
                                throw new IllegalArgumentException("'xzSpread' and 'ySpread' must be in range [-16, 16]");
                            return RandomOffsetPlacement.of(xz, y);
                        },
                        "Add a 'minecraft:random_offset' placement modifier"
                )
                .<IntProvider>registerSingleArg(
                        "verticalRandomOffset",
                        vs,
                        i -> {
                            if (i.getMinValue() < -16 || i.getMaxValue() > 16)
                                throw new IllegalArgumentException("'ySpread' must be in the range [-16, 16]");
                            return RandomOffsetPlacement.vertical(i);
                        },
                        "Add a purely vertical 'minecraft:random_offset' placement modifier"
                )
                .<IntProvider>registerSingleArg(
                        "horizontalRandomOffset",
                        hs,
                        i -> {
                            if (i.getMinValue() < -16 || i.getMaxValue() > 16)
                                throw new IllegalArgumentException("'xzSpread' must be in the range [-16, 16]");
                            return RandomOffsetPlacement.horizontal(i);
                        },
                        "Add a purely horizontal 'minecraft:random_offset' placement modifier"
                )
                .<IntProvider>registerSingleArg(
                        "countOnEveryLayer",
                        "count",
                        Wrappers.INT_PROVIDER,
                        "The number of times to place per layer",
                        i -> {
                            if (i.getMinValue() < 0 || i.getMaxValue() > 256)
                                throw new IllegalArgumentException("'count' must be in the range [0, 256]");
                            return CountOnEveryLayerPlacement.of(i);
                        },
                        "Add a 'minecraft:count_on_every_layer' placement modifier"
                )
                .register(
                        "environmentScan",
                        event.arg(ds = event.singleArg("directionOfSearch", Direction.class, "The direction to search in"))
                                .arg(tc = event.singleArg("targetCondition", BLOCK_PREDICATE, "the condition for a valid block"))
                                .arg("allowedSearchCondition", BLOCK_PREDICATE, "the condition that steps in the scan must pass")
                                .arg(ms = event.singleArg("maxSteps", int.class, "The maximum number of blocks, in the range [1, 32], out from the original position to check")),
                        a -> {
                            final int step = i(a[3]);
                            if (step < 1 || step > 32)
                                throw new IllegalArgumentException("'maxSteps' must be in the range [1, 32]");
                            return EnvironmentScanPlacement.scanningFor(
                                    Cast.to(a[0]),
                                    Cast.to(a[1]),
                                    Cast.to(a[2]),
                                    step
                            );
                        },
                        "Add a 'minecraft:environment_scan' placement modifier"
                )
                .register(
                        "environmentScan",
                        event.arg(ds).arg(tc).arg(ms),
                        a -> {
                            final int step = i(a[2]);
                            if (step < 1 || step > 32)
                                throw new IllegalArgumentException("'maxSteps' must be in the range [1, 32]");
                            return EnvironmentScanPlacement.scanningFor(
                                    Cast.to(a[0]),
                                    Cast.to(a[1]),
                                    step
                            );
                        },
                        "Add a 'minecraft:environment_scan' placement modifier"
                )
                .register(
                        "surfaceRelativeThreshold",
                        event.arg(rth = event.singleArg("heightmap", HEIGHTMAP, "The heightmap to be within range of"))
                                .arg(rti = event.singleArg("minInclusive", int.class, "The minimum relative height from the surface to the position"))
                                .arg(rta = event.singleArg("maxInclusive", int.class, "The maximum relative height from the surface to the position")),
                        a -> SurfaceRelativeThresholdFilter.of(
                                Cast.to(a[0]),
                                i(a[1]),
                                i(a[2])
                        ),
                        "Add a 'minecraft:surface_relative_threshold_filter' placement filter"
                )
                .register(
                        "surfaceRelativeThresholdMax",
                        event.arg(rth).arg(rta),
                        a -> SurfaceRelativeThresholdFilter.of(
                                Cast.to(a[0]),
                                Integer.MIN_VALUE,
                                i(a[1])
                        ),
                        "Add a 'minecraft:surface_relative_threshold_filter' placement filter with no minimum bound"
                )
                .register(
                        "surfaceRelativeThresholdMin",
                        event.arg(rth).arg(rti),
                        a -> SurfaceRelativeThresholdFilter.of(
                                Cast.to(a[0]),
                                i(a[1]),
                                Integer.MAX_VALUE
                        ),
                        "Add a 'minecraft:surface_relative_threshold_filter' placement filter with no maximum bound"
                )
                .registerSingleArg(
                        "surfaceWaterDepth",
                        "maxWaterDepth",
                        int.class,
                        "The maximum water depth of water under which the feature can be placed",
                        SurfaceWaterDepthFilter::forMaxDepth,
                        "Add a 'minecraft:surface_water_depth_filter' placement filter"
                )
                .registerSingleArg(
                        "blockPredicate",
                        "predicate",
                        BLOCK_PREDICATE,
                        "The block validator for placement",
                        BlockPredicateFilter::forPredicate,
                        "Add a 'minecraft:block_predicate_filter' placement filter"
                )
                .registerSingleArg(
                        "heightRange",
                        "height",
                        HeightProvider.class,
                        "The height range over which the feature can place",
                        HeightRangePlacement::of,
                        "Add a 'minecraft:height_range' placement modifier"
                )
                .register(
                        "uniformHeightRange",
                        anchorRange,
                        a -> HeightRangePlacement.uniform(
                                Cast.to(a[0]),
                                Cast.to(a[1])
                        ),
                        "Add a 'minecraft:height_range' placement modifier which has a uniform chance of placing the feature anywhere within the bounds"
                )
                .register(
                        "triangleHeightRange",
                        anchorRange,
                        a -> HeightRangePlacement.triangle(
                                Cast.to(a[0]),
                                Cast.to(a[1])
                        ),
                        "Add a 'minecraft:height_range' placement modifier which has the highest chance of placing the feature in the center of bounds"
                )
                .<VerticalAnchor>registerSingleArg(
                        "constantHeightRange",
                        "height",
                        Wrappers.VERTICAL_ANCHOR,
                        "The height to place at",
                        v -> HeightRangePlacement.of(ConstantHeight.of(v)),
                        "Add a 'minecraft:height_range' placement modifier which places the feature at the exact height given"
                )
        ;
    }

    private static IntProvider intProvider(Object o) {
        return Cast.to(o);
    }

    private static int i(Object o) {
        return (int) o;
    }

    private static double d(Object o) {
        return (double) o;
    }
}
