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
    private static final TypeInfo DIRECTION = TypeInfo.of(Direction.class);
    private static final TypeInfo HEIGHTMAP = TypeInfo.of(Heightmap.Types.class);

    // TODO: 1.0.0 | Finish infos
    private void addVanillaPlacementModifiers(PlacedFeatureModifierEvent event) {
        event.namespace("minecraft")
                .unit("biome", BiomeFilter.biome(), event.info("Add a 'minecraft:biome' placement filter"))
                .unit("inSquare", InSquarePlacement.spread(), event.info("Add a 'minecraft:in_square' placement modifier"))
                .<IntProvider>registerSingleArg(
                        "count",
                        Wrappers.INT_PROVIDER,
                        "count",
                        CountPlacement::of,
                        event.info(
                                "Add a 'minecraft:count' placement modifier",
                                event.param("count", "How many times the placed feature should be placed")
                        )
                )
                .registerSingleArg(
                        "fixed",
                        BlockPos[].class,
                        "positions",
                        FixedPlacement::of,
                        event.info(
                                "Add a 'minecraft:fixed_placement' placement modifier",
                                event.param("positions", "The positions to place the feature at")
                        )
                )
                .registerSingleArg(
                        "rarityFilter",
                        int.class,
                        "chance",
                        i -> {
                            if (i < 1) throw new IllegalArgumentException("'chance' must be positive");
                            return RarityFilter.onAverageOnceEvery(i);
                        },
                        event.info(
                                "Add a 'minecraft:rarity_filter' placement filter",
                                event.param("chance", "The chance the feature will successfully place as `1/chance`")
                        )
                )
                .registerSingleArg(
                        "carvingMask",
                        GenerationStep.Carving.class,
                        "carvingStep",
                        CarvingMaskPlacement::forStep,
                        event.info(
                                "Add a 'minecraft:carving_mask' placement modifier",
                                event.param("carvingStep", "The carving step volume for which the feature can place")
                        )
                )
                .registerSingleArg(
                        "heightmap",
                        HEIGHTMAP,
                        "heightmap",
                        HeightmapPlacement::onHeightmap,
                        event.info(
                                "Add a 'minecraft:heightmap' placement modifier",
                                event.param("heightmap", "The heightmap to place the feature at")
                        )
                )
                .register(
                        "noiseBasedCount",
                        new TypeInfo[] {
                                TypeInfo.PRIMITIVE_INT,
                                TypeInfo.PRIMITIVE_DOUBLE,
                                TypeInfo.PRIMITIVE_DOUBLE
                        },
                        new String[] {
                                "noiseToCountRatio",
                                "noiseFactor",
                                "noiseOffset"
                        },
                        o -> NoiseBasedCountPlacement.of(
                                i(o[0]),
                                d(o[1]),
                                d(o[2])
                        ),
                        event.info(
                                "Add a 'minecraft:noise_based_count' placement modifier",
                                event.param("noiseToCountRatio", ),
                                event.param("noiseFactor", ),
                                event.param("noiseOffset", )
                        )
                )
                .register(
                        "noiseBasedCount",
                        new TypeInfo[] {
                                TypeInfo.PRIMITIVE_INT,
                                TypeInfo.PRIMITIVE_DOUBLE
                        },
                        new String[] {
                                "noiseToCountRatio",
                                "noiseFactor"
                        },
                        o -> NoiseBasedCountPlacement.of(
                                i(o[0]),
                                d(o[1]),
                                0.0
                        ),
                        event.info(
                                "Add a 'minecraft:noise_based_count' placement modifier",
                                event.param("noiseTpCountRatio", ),
                                event.param("noiseFactor", )
                        )
                )
                .register(
                        "noiseThresholdCount",
                        new TypeInfo[] {
                                TypeInfo.PRIMITIVE_DOUBLE,
                                TypeInfo.PRIMITIVE_INT,
                                TypeInfo.PRIMITIVE_INT
                        },
                        new String[] {
                                "noiseLevel",
                                "belowNoise",
                                "aboveNoise"
                        },
                        o -> NoiseThresholdCountPlacement.of(
                                d(o[0]),
                                i(o[1]),
                                i(o[2])
                        ),
                        event.info(
                                "Add a 'minecraft:noise_threshold_count' placement modifier",
                                event.param("noiseLevel", ),
                                event.param("belowNoise", ),
                                event.param("aboveNoise", )
                        )
                )
                .register(
                        "randomOffset",
                        new TypeInfo[] {
                                Wrappers.INT_PROVIDER,
                                Wrappers.INT_PROVIDER
                        },
                        new String[] {
                                "xzSpread",
                                "ySpread"
                        },
                        o -> {
                            final IntProvider xz = intProvider(o[0]), y = intProvider(o[1]);
                            if (xz.getMinValue() < -16 || y.getMinValue() < -16 || xz.getMaxValue() > 16 || y.getMaxValue() > 16)
                                throw new IllegalArgumentException("'xzSpread' and 'ySpread' must be in range [-16, 16]");
                            return RandomOffsetPlacement.of(xz, y);
                        },
                        event.info(
                                "Add a 'minecraft:random_offset' placement modifier",
                                event.param("xzSpread", "The horizontal spread"),
                                event.param("ySpread", "The vertical spread")
                        )
                )
                .<IntProvider>registerSingleArg(
                        "verticalRandomOffset",
                        Wrappers.INT_PROVIDER,
                        "ySpread",
                        i -> {
                            if (i.getMinValue() < -16 || i.getMaxValue() > 16)
                                throw new IllegalArgumentException("'ySpread' must be in the range [-16, 16]");
                            return RandomOffsetPlacement.vertical(i);
                        },
                        event.info(
                                "Add a purely vertical 'minecraft:random_offset' placement modifier",
                                event.param("ySpread", "The vertical spread")
                        )
                )
                .<IntProvider>registerSingleArg(
                        "horizontalRandomOffset",
                        Wrappers.INT_PROVIDER,
                        "xzSpread",
                        i -> {
                            if (i.getMinValue() < -16 || i.getMaxValue() > 16)
                                throw new IllegalArgumentException("'xzSpread' must be in the range [-16, 16]");
                            return RandomOffsetPlacement.horizontal(i);
                        },
                        event.info(
                                "Add a purely horizontal 'minecraft:random_offset' placement modifier",
                                event.param("xzSpread", "The horizontal spread")
                        )
                )
                .<IntProvider>registerSingleArg(
                        "countOnEveryLayer",
                        Wrappers.INT_PROVIDER,
                        "count",
                        i -> {
                            if (i.getMinValue() < 0 || i.getMaxValue() > 256)
                                throw new IllegalArgumentException("'count' must be in the range [0, 256]");
                            return CountOnEveryLayerPlacement.of(i);
                        },
                        event.info(
                                "Add a 'minecraft:count_on_every_layer' placement modifier",
                                event.param("count", "The number of time to place per layer")
                        )
                )
                .register(
                        "environmentScan",
                        new TypeInfo[] {
                                DIRECTION,
                                BLOCK_PREDICATE,
                                BLOCK_PREDICATE,
                                TypeInfo.PRIMITIVE_INT
                        },
                        new String[] {
                                "directionOfSearch",
                                "targetCondition",
                                "allowedSearchCondition",
                                "maxSteps"
                        },
                        o -> {
                            final int step = i(o[3]);
                            if (step < 1 || step > 32) throw new IllegalArgumentException("'maxSteps' must be in range [1, 32]");
                            return EnvironmentScanPlacement.scanningFor(
                                    Cast.to(o[0]),
                                    Cast.to(o[1]),
                                    Cast.to(o[2]),
                                    step
                            );
                        },
                        event.info(
                                "Add a 'minecraft:environment_scan' placement modifier",
                                event.param("directionOfSearch", ),
                                event.param("targetCondition", ),
                                event.param("allowedSearchCondition", ),
                                event.param("maxSteps", )
                        )
                )
                .register(
                        "environmentScan",
                        new TypeInfo[] {
                                DIRECTION,
                                BLOCK_PREDICATE,
                                TypeInfo.PRIMITIVE_INT
                        },
                        new String[] {
                                "directionOfSearch",
                                "targetCondition",
                                "maxSteps"
                        },
                        o -> {
                            final int step = i(o[2]);
                            if (step < 1 || step > 32) throw new IllegalArgumentException("'maxSteps' must be in range [1, 32]");
                            return EnvironmentScanPlacement.scanningFor(
                                    Cast.to(o[0]),
                                    Cast.to(o[1]),
                                    step
                            );
                        },
                        event.info(
                                "Add a 'minecraft:environment_scan' placement modifier",
                                event.param("directionOfSearch", ),
                                event.param("targetCondition", ),
                                event.param("maxSteps", )
                        )
                )
                .register(
                        "surfaceRelativeThreshold",
                        new TypeInfo[] {
                                HEIGHTMAP,
                                TypeInfo.PRIMITIVE_INT,
                                TypeInfo.PRIMITIVE_INT
                        },
                        new String[] {
                                "heightmap",
                                "minInclusive",
                                "maxInclusive"
                        },
                        o -> SurfaceRelativeThresholdFilter.of(
                                Cast.to(o[0]),
                                i(o[1]),
                                i(o[2])
                        ),
                        event.info(
                                "Add a 'minecraft:surface_relative_threshold_filter' placement filter",
                                event.param("heightmap", ),
                                event.param("minInclusive", ),
                                event.param("maxInclusive", )
                        )
                )
                .register(
                        "surfaceRelativeThresholdMax",
                        new TypeInfo[] {
                                HEIGHTMAP,
                                TypeInfo.PRIMITIVE_INT
                        },
                        new String[] {
                                "heightmap",
                                "maxInclusive"
                        },
                        o -> SurfaceRelativeThresholdFilter.of(
                                Cast.to(o[0]),
                                Integer.MIN_VALUE,
                                i(o[1])
                        ),
                        event.info(
                                "Add a 'minecraft:surface_relative_threshold_filter' placement filter with no minimum bound",
                                event.param("heightmap", ),
                                event.param("maxInclusive", )
                        )
                )
                .register(
                        "surfaceRelativeThresholdMin",
                        new TypeInfo[] {
                                HEIGHTMAP,
                                TypeInfo.PRIMITIVE_INT
                        },
                        new String[] {
                                "heightmap",
                                "minInclusive"
                        },
                        o -> SurfaceRelativeThresholdFilter.of(
                                Cast.to(o[0]),
                                i(o[1]),
                                Integer.MAX_VALUE
                        ),
                        event.info(
                                "Add a 'minecraft:surface_relative_threshold_filter' placement filter with no maximum bound",
                                event.param("heightmap", ),
                                event.param("minInclusive", )
                        )
                )
                .<Heightmap.Types>registerSingleArg(
                        "surfaceRelativeThreshold",
                        HEIGHTMAP,
                        "heightmap",
                        m -> SurfaceRelativeThresholdFilter.of(
                                m,
                                Integer.MIN_VALUE,
                                Integer.MAX_VALUE
                        ),
                        event.info(
                                "Add a 'minecraft:surface_relative_threshold_filter' placement filter with no bounds",
                                event.param("heightmap", )
                        )
                )
                .registerSingleArg(
                        "surfaceWaterDepth",
                        int.class,
                        "maxWaterDepth",
                        SurfaceWaterDepthFilter::forMaxDepth,
                        event.info(
                                "Add a 'minecraft:surface_water_depth_filter' placement filter",
                                event.param("maxWaterDepth", "The maximum depth of water under which the feature can be placed")
                        )
                )
                .registerSingleArg(
                        "blockPredicate",
                        BLOCK_PREDICATE,
                        "predicate",
                        BlockPredicateFilter::forPredicate,
                        event.info(
                                "Add a 'minecraft:block_predicate_filter' placement filter",
                                event.param("predicate", "The block validator for placement")
                        )
                )
                .registerSingleArg(
                        "heightRange",
                        HeightProvider.class,
                        "height",
                        HeightRangePlacement::of,
                        event.info(
                                "Add a 'minecraft:height_range' placement modifier",
                                event.param("height", "The height range over which the feature may place")
                        )
                )
                .register(
                        "uniformHeightRange",
                        new TypeInfo[] {
                                Wrappers.VERTICAL_ANCHOR,
                                Wrappers.VERTICAL_ANCHOR
                        },
                        new String[] {
                                "minInclusive",
                                "maxInclusive"
                        },
                        o -> HeightRangePlacement.uniform(
                                Cast.to(o[0]),
                                Cast.to(o[1])
                        ),
                        event.info(
                                "Add a 'minecraft:height_range' placement modifier which has a uniform chance of placing the feature anywhere over the bounds",
                                event.param("minInclusive", "The lower placement bound"),
                                event.param("maxInclusive", "The upper placement bound")
                        )
                )
                .register(
                        "triangleHeightRange",
                        new TypeInfo[] {
                                Wrappers.VERTICAL_ANCHOR,
                                Wrappers.VERTICAL_ANCHOR
                        },
                        new String[] {
                                "minInclusive",
                                "maxInclusive"
                        },
                        o -> HeightRangePlacement.triangle(
                                Cast.to(o[0]),
                                Cast.to(o[1])
                        ),
                        event.info(
                                "Add a 'minecraft:height_range' placement modifier which has the highest chance of placing the feature in the center of bounds",
                                event.param("minInclusive", "The lower placement bound"),
                                event.param("maxInclusive", "The upper placement bound")
                        )
                )
                .<VerticalAnchor>registerSingleArg(
                        "constantHeightRange",
                        Wrappers.VERTICAL_ANCHOR,
                        "height",
                        a -> HeightRangePlacement.of(ConstantHeight.of(a)),
                        event.info(
                                "Add a 'minecraft:height_range' placement modifier which places the feature at the exact height given",
                                event.param("height", "The height to place the feature at")
                        )
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
