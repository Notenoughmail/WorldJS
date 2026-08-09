package io.github.notenoughmail.worldjs;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.util.Cast;
import io.github.notenoughmail.worldjs.builders.bs.CheckerboardBiomeSourceBuilder;
import io.github.notenoughmail.worldjs.builders.bs.EndBiomeSourceBuilder;
import io.github.notenoughmail.worldjs.builders.bs.FixedBiomeSourceBuilder;
import io.github.notenoughmail.worldjs.builders.bs.MultiNoiseBiomeSourceBuilder;
import io.github.notenoughmail.worldjs.builders.cg.DebugChunkGeneratorBuilder;
import io.github.notenoughmail.worldjs.builders.cg.FlatChunkGeneratorBuilder;
import io.github.notenoughmail.worldjs.builders.cg.NoiseBasedChunkGeneratorBuilder;
import io.github.notenoughmail.worldjs.types.features.WeightedRandomSelectorFeature;
import io.github.notenoughmail.worldjs.util.Validations;
import io.github.notenoughmail.worldjs.util.event.BiomeSourceTypeRegisterEvent;
import io.github.notenoughmail.worldjs.util.event.ChunkGeneratorTypeRegisterEvent;
import io.github.notenoughmail.worldjs.util.event.PlacedFeatureModifierEvent;
import io.github.notenoughmail.worldjs.util.synmethod.Args;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.placement.*;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.github.notenoughmail.worldjs.util.Types.*;

@Mod(WorldJS.MODID)
public class WorldJS {

    public static final String MODID = "worldjs";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation identifier(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static ResourceLocation mc(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }

    private static final Set<Throwable> THROWN = new HashSet<>();

    public static void scriptErrorOnce(String msg, Throwable t) {
        if (!THROWN.contains(t)) {
            THROWN.add(t);
            ConsoleJS.SERVER.error(msg, t);
        }
    }

    public static RuntimeException irrecoverableError(String msg, Throwable t) {
        LOGGER.error(msg, t);
        return new RuntimeException(t);
    }

    private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, MODID);

    public static final DeferredHolder<Feature<?>, WeightedRandomSelectorFeature> WEIGHTED_RANDOM_SELECTOR = FEATURES.register("weighted_random_selector", () -> new WeightedRandomSelectorFeature(WeightedRandomSelectorFeature.Configuration.CODEC));

    public WorldJS(IEventBus modBus) {
        NeoForge.EVENT_BUS.addListener(this::addVanillaPlacementModifiers);
        NeoForge.EVENT_BUS.addListener(this::addVanillaChunkGeneratorTypes);
        NeoForge.EVENT_BUS.addListener(this::addVanillaBiomeSourceTypes);

        FEATURES.register(modBus);
    }

    public static <V, E extends Event> Supplier<Map<ResourceLocation, V>> eventMap(Function<BiConsumer<ResourceLocation, V>, E> eventConstructor) {
        return Suppliers.memoize(() ->
                Util.make(
                        ImmutableMap.<ResourceLocation, V>builder(),
                        m -> NeoForge.EVENT_BUS.post(eventConstructor.apply(m::put))
                ).build()
        );
    }

    private void addVanillaPlacementModifiers(PlacedFeatureModifierEvent event) {

        final Args anchorRange =
                event.arg("minInclusive", VERTICAL_ANCHOR, "The lower placement bound")
                        .arg("maxInclusive", VERTICAL_ANCHOR, "The upper placement bound");

        final Args.Arg n2c, nf, hs, vs, ds, tc, ms, rth, rti, rta;

        var mc = event.namespace("minecraft")
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
                        INT_PROVIDER,
                        "How many times the placed feature should be placed",
                        i -> CountPlacement.of(
                                Validations.assertRange(i, 0, 256, "count")
                        ),
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
                .<Integer>registerSingleArg(
                        "rarityFilter",
                        "chance",
                        INT,
                        "The chance the feature will successfully place as `1/chance`",
                        i -> RarityFilter.onAverageOnceEvery(
                                Validations.assertPositive(i, "chance")
                        ),
                        "Add a 'minecraft:rarity_filter' placement filter"
                )
                .registerSingleArg(
                        "carvingMask",
                        "carvingStep",
                        GenerationStep.Carving.class,
                        "The carving step volume for which the feature will try to place in",
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
                        event.arg(n2c = event.singleArg("noiseToCountRatio", INT, "Ratio of noise value to count"))
                                .arg(nf = event.singleArg("noiseFactor", DOUB, "Horizontal scale factor of the noise. Higher values make wider, more spaced out peaks"))
                                .arg("noiseOffset", DOUB, "Vertical offset of the noise. Optional, defaults to 0"),
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
                        event.arg("noiseLevel", DOUB, "The threshold for determining if to use `belowNoise` or `aboveNoise`")
                                .arg("belowNoise", INT, "The count used when below the threshold")
                                .arg("aboveNoise", INT, "The count used when above the threshold"),
                        a -> NoiseThresholdCountPlacement.of(
                                d(a[0]),
                                i(a[1]),
                                i(a[2])
                        ),
                        "Add a 'minecraft:noise_threshold_count' placement modifier"
                )
                .register(
                        "randomOffset",
                        event.arg(hs = event.singleArg("xzSpread", INT_PROVIDER, "The horizontal spread"))
                                .arg(vs = event.singleArg("ySpread", INT_PROVIDER, "The vertical spread")),
                        a -> RandomOffsetPlacement.of(
                                Validations.assertRange(intProvider(a[0]), -16, 16, "xzSpread"),
                                Validations.assertRange(intProvider(a[1]), -16, 16, "ySpread")
                        ),
                        "Add a 'minecraft:random_offset' placement modifier"
                )
                .<IntProvider>registerSingleArg(
                        "verticalRandomOffset",
                        vs,
                        i -> RandomOffsetPlacement.vertical(
                                Validations.assertRange(i, -16, 16, "ySpread")
                        ),
                        "Add a purely vertical 'minecraft:random_offset' placement modifier"
                )
                .<IntProvider>registerSingleArg(
                        "horizontalRandomOffset",
                        hs,
                        i -> RandomOffsetPlacement.horizontal(
                                Validations.assertRange(i, -16, 16, "xzSpread")
                        ),
                        "Add a purely horizontal 'minecraft:random_offset' placement modifier"
                )
                .<IntProvider>registerSingleArg(
                        "countOnEveryLayer",
                        "count",
                        INT_PROVIDER,
                        "The number of times to place per layer",
                        i -> CountOnEveryLayerPlacement.of(
                                Validations.assertRange(i, 0, 256, "count")
                        ),
                        "Add a 'minecraft:count_on_every_layer' placement modifier"
                )
                .register(
                        "environmentScan",
                        event.arg(ds = event.singleArg("directionOfSearch", DIRECTION, "The direction to search in"))
                                .arg(tc = event.singleArg("targetCondition", BLOCK_PREDICATE, "the condition for a valid block"))
                                .arg("allowedSearchCondition", BLOCK_PREDICATE, "the condition that steps in the scan must pass")
                                .arg(ms = event.singleArg("maxSteps", INT, "The maximum number of blocks, in the range [1, 32], out from the original position to check")),
                        a -> EnvironmentScanPlacement.scanningFor(
                                Cast.to(a[0]),
                                Cast.to(a[1]),
                                Cast.to(a[2]),
                                Validations.assertRange(i(a[3]), 1, 32, "maxSteps")
                        ),
                        "Add a 'minecraft:environment_scan' placement modifier"
                )
                .register(
                        "environmentScan",
                        event.arg(ds).arg(tc).arg(ms),
                        a -> EnvironmentScanPlacement.scanningFor(
                                Cast.to(a[0]),
                                Cast.to(a[1]),
                                Validations.assertRange(i(a[2]), 1, 32, "maxSteps")
                        ),
                        "Add a 'minecraft:environment_scan' placement modifier"
                )
                .register(
                        "surfaceRelativeThreshold",
                        event.arg(rth = event.singleArg("heightmap", HEIGHTMAP, "The heightmap to be within range of"))
                                .arg(rti = event.singleArg("minInclusive", INT, "The minimum relative height from the surface to the position"))
                                .arg(rta = event.singleArg("maxInclusive", INT, "The maximum relative height from the surface to the position")),
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
                        INT,
                        "The maximum depth of water under which the feature can be placed",
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
                        VERTICAL_ANCHOR,
                        "The height to place at",
                        v -> HeightRangePlacement.of(ConstantHeight.of(v)),
                        "Add a 'minecraft:height_range' placement modifier which places the feature at the exact height given"
                )
        ;

        // if (!FMLEnvironment.production) {
        //     mc.printAll();
        // }
    }

    private void addVanillaChunkGeneratorTypes(ChunkGeneratorTypeRegisterEvent event) {
        event.register(
                mc("debug"),
                DebugChunkGeneratorBuilder.class,
                DebugChunkGeneratorBuilder::new
        );
        event.register(
                mc("flat"),
                FlatChunkGeneratorBuilder.class,
                FlatChunkGeneratorBuilder::new
        );
        event.register(
                mc("noise"),
                NoiseBasedChunkGeneratorBuilder.class,
                NoiseBasedChunkGeneratorBuilder::new
        );
    }

    private void addVanillaBiomeSourceTypes(BiomeSourceTypeRegisterEvent event) {
        event.register(
                mc("the_end"),
                EndBiomeSourceBuilder.class,
                EndBiomeSourceBuilder::new
        );
        event.register(
                mc("fixed"),
                FixedBiomeSourceBuilder.class,
                FixedBiomeSourceBuilder::new
        );
        event.register(
                mc("checkerboard"),
                CheckerboardBiomeSourceBuilder.class,
                CheckerboardBiomeSourceBuilder::new
        );
        event.register(
                mc("multi_noise"),
                MultiNoiseBiomeSourceBuilder.class,
                MultiNoiseBiomeSourceBuilder::new
        );
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
