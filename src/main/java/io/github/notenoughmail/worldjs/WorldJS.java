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

    private void addVanillaPlacementModifiers(PlacedFeatureModifierEvent event) {
        event.namespace("minecraft")
                .unit("biome", BiomeFilter.biome())
                .unit("inSquare", InSquarePlacement.spread())
                .<IntProvider>registerSingleArg(
                        "count",
                        Wrappers.INT_PROVIDER,
                        "count",
                        CountPlacement::of
                )
                .registerSingleArg(
                        "fixed",
                        BlockPos[].class,
                        "positions",
                        FixedPlacement::of
                )
                .registerSingleArg(
                        "rarityFilter",
                        int.class,
                        "chance",
                        i -> {
                            if (i < 1) throw new IllegalArgumentException("'chance' must be positive");
                            return RarityFilter.onAverageOnceEvery(i);
                        }
                )
                .registerSingleArg(
                        "carvingMask",
                        GenerationStep.Carving.class,
                        "carvingStep",
                        CarvingMaskPlacement::forStep
                )
                .registerSingleArg(
                        "heightmap",
                        HEIGHTMAP,
                        "heightmap",
                        HeightmapPlacement::onHeightmap
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
                        }
                )
                .<IntProvider>registerSingleArg(
                        "verticalRandomOffset",
                        Wrappers.INT_PROVIDER,
                        "ySpread",
                        i -> {
                            if (i.getMinValue() < -16 || i.getMaxValue() > 16)
                                throw new IllegalArgumentException("'ySpread' must be in the range [-16, 16]");
                            return RandomOffsetPlacement.vertical(i);
                        }
                )
                .<IntProvider>registerSingleArg(
                        "horizontalRandomOffset",
                        Wrappers.INT_PROVIDER,
                        "xzSpread",
                        i -> {
                            if (i.getMinValue() < -16 || i.getMaxValue() > 16)
                                throw new IllegalArgumentException("'xzSpread' must be in the range [-16, 16]");
                            return RandomOffsetPlacement.horizontal(i);
                        }
                )
                .<IntProvider>registerSingleArg(
                        "countOnEveryLayer",
                        Wrappers.INT_PROVIDER,
                        "count",
                        i -> {
                            if (i.getMinValue() < 0 || i.getMaxValue() > 256)
                                throw new IllegalArgumentException("'count' must be in the range [0, 256]");
                            return CountOnEveryLayerPlacement.of(i);
                        }
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
                        }
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
                        }
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
                        )
                )
                .registerSingleArg(
                        "surfaceWaterDepth",
                        int.class,
                        "maxWaterDepth",
                        SurfaceWaterDepthFilter::forMaxDepth
                )
                .registerSingleArg(
                        "blockPredicate",
                        BLOCK_PREDICATE,
                        "predicate",
                        BlockPredicateFilter::forPredicate
                )
                .registerSingleArg(
                        "heightRange",
                        HeightProvider.class,
                        "height",
                        HeightRangePlacement::of
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
                        )
                )
                .<VerticalAnchor>registerSingleArg(
                        "constantHeightRange",
                        Wrappers.VERTICAL_ANCHOR,
                        "height",
                        a -> HeightRangePlacement.of(ConstantHeight.of(a))
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
