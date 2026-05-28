package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.List;

@ReturnsSelf
public class GeodeConfigurationBuilder extends ConfiguredFeatureBuilder<GeodeConfiguration> {

    public transient double usePotentialPlacementsChance,
            useAlternativeLayer0Chance,
            noiseMultiplier;
    public transient boolean placementsRequireLayer0Alternative;
    public transient int minGenOffset, maxGenOffset, invalidBlocksThreshold;
    public transient IntProvider outWallDistance, distributionPoints, pointOffset;
    public transient GeodeBlocks blocks;
    public transient GeodeLayers layers;
    public transient GeodeCrack crack;

    public GeodeConfigurationBuilder(ResourceLocation id) {
        super(id);
        usePotentialPlacementsChance = 0.35D;
        useAlternativeLayer0Chance = 0D;
        noiseMultiplier = 0.05D;
        placementsRequireLayer0Alternative = true;
        minGenOffset = -16;
        maxGenOffset = 16;
        blocks = GeodeBlocks.DEFAULT;
        layers = GeodeLayers.DEFAULT;
        crack = GeodeCrack.DEFAULT;
        outWallDistance = UniformInt.of(4, 5);
        distributionPoints = UniformInt.of(3, 4);
        pointOffset = UniformInt.of(1, 2);
    }

    @Info("The blocks used for the geode")
    public GeodeConfigurationBuilder blocks(GeodeBlocks blocks) {
        notEmpty(blocks.innerPlacements(), "blocks.innerPlacements");
        this.blocks = blocks;
        return this;
    }

    private void assertLayer(double d, String name) {
        assertRange(d, 0.01D, 50D, "layers." + name);
    }

    @Info("The thickness of each layer")
    public GeodeConfigurationBuilder layers(GeodeLayers layers) {
        assertLayer(layers.filling(), "filling");
        assertLayer(layers.innerLayer(), "innerLayer");
        assertLayer(layers.middleLayer(), "middleLayer");
        assertLayer(layers.outerLayer(), "outerLayer");
        this.layers = layers;
        return this;
    }

    @Info("The crack properties")
    public GeodeConfigurationBuilder crack(GeodeCrack crack) {
        assertUnit(crack.generateChance(), "crack.generateChance");
        assertRange(crack.baseSize(), 0D, 5D, "crack.baseSize");
        assertRange(crack.pointOffset(), 0, 10, "crack.pointOffset");
        this.crack = crack;
        return this;
    }

    @Info("The probability of placing an inner placement on a block of the inner layer, in the range [0, 1]. Defaults to 0.35")
    public GeodeConfigurationBuilder usePotentialPlacementsChance(double chance) {
        usePotentialPlacementsChance = assertUnit(chance, "usePotentialPlacementsChance");
        return this;
    }

    @Info("The chance to place an alternative inner layer block instead of a regular inner layer block, in the range [0, 1]. Defaults to 0")
    public GeodeConfigurationBuilder useAlternativeLayer0Chance(double chance) {
        useAlternativeLayer0Chance = assertUnit(chance, "useAlternativeLayer0Chance");
        return this;
    }

    @Info("The offset on each coordinate of the center from the feature start, in the range [1, 20]. Defaults to uniformly over {4, 5}")
    public GeodeConfigurationBuilder outerWallDistance(IntProvider provider) {
        outWallDistance = assertRange(provider, 1, 20, "outerWallDistance");
        return this;
    }

    @Info("The number of distribution points, in the range [1, 20]. Defaults to uniformly over {3, 4}")
    public GeodeConfigurationBuilder distributionPoints(IntProvider provider) {
        distributionPoints = assertRange(provider, 1, 20, "distributionPoints");
        return this;
    }

    @Info("The point offset, in the range [0, 10]. Defaults to uniformly over {1, 2}")
    public GeodeConfigurationBuilder pointOffset(IntProvider provider) {
        pointOffset = assertRange(provider, 0, 10, "pointOffset");
        return this;
    }

    @Info("The minimum Chebyshev distance between the block and the center. Defaults to -16")
    public GeodeConfigurationBuilder minGenOffset(int offset) {
        minGenOffset = offset;
        return this;
    }

    @Info("The maximum Chebyshev distance between the block and the center. Defaults to 16")
    public GeodeConfigurationBuilder maxGenOffset(int offset) {
        maxGenOffset = offset;
        return this;
    }

    @Info("The noise multiplier, in the range [0, 1]. Defaults to 0.05")
    public GeodeConfigurationBuilder noiseMultiplier(double multiplier) {
        noiseMultiplier = assertUnit(multiplier, "noiseMultiplier");
        return this;
    }

    @Info("The threshold for invalid blocks found by checking near the geode distributionPoints times near the center of geode above which the geode will not generate. Defaults to 0")
    public GeodeConfigurationBuilder invalidBlocksThreshold(int blocks) {
        invalidBlocksThreshold = blocks;
        return this;
    }

    @Override
    protected GeodeConfiguration createFeatureConfiguration() {
        return new GeodeConfiguration(
                blocks.build(),
                layers.build(),
                crack.build(),
                usePotentialPlacementsChance,
                useAlternativeLayer0Chance,
                placementsRequireLayer0Alternative,
                outWallDistance,
                distributionPoints,
                pointOffset,
                minGenOffset,
                maxGenOffset,
                noiseMultiplier,
                invalidBlocksThreshold
        );
    }

    @Override
    protected Feature<GeodeConfiguration> getFeature() {
        return Feature.GEODE;
    }

    public record GeodeBlocks(
            @Info("The blocks of the 'filling' layer, air by default")
            BlockStateProvider filling,
            @Info("The blocks of the inner layer, amethyst by default")
            BlockStateProvider innerLayer,
            @Info("The alternative blocks of the inner layer, budding amethyst by default")
            BlockStateProvider alternativeInnerLayer,
            @Info("The blocks of the middle layer, calcite by default")
            BlockStateProvider middleLayer,
            @Info("The blocks of the outer layer, smooth basalt by default")
            BlockStateProvider outerLayer,
            @Info("The blocks to place adjacent to the alternative inner layer blocks, amethyst buds and clusters by default")
            List<BlockState> innerPlacements,
            @Info("The blocks that the geode cannot replace, defaults to `minecraft:features_cannot_replace`")
            TagKey<Block> cannotReplace,
            @Info("Blocks which the geode considers invalid. Is not respected due to MC-264886. Defaults to `minecraft:geode_invalid_blocks`")
            TagKey<Block> invalidBlocks
    ) {
        public static final GeodeBlocks DEFAULT = new GeodeBlocks(
                BlockStateProvider.simple(Blocks.AIR),
                BlockStateProvider.simple(Blocks.AMETHYST_BLOCK),
                BlockStateProvider.simple(Blocks.BUDDING_AMETHYST),
                BlockStateProvider.simple(Blocks.CALCITE),
                BlockStateProvider.simple(Blocks.SMOOTH_BASALT),
                List.of(
                        Blocks.SMALL_AMETHYST_BUD.defaultBlockState(),
                        Blocks.MEDIUM_AMETHYST_BUD.defaultBlockState(),
                        Blocks.LARGE_AMETHYST_BUD.defaultBlockState(),
                        Blocks.AMETHYST_CLUSTER.defaultBlockState()
                ),
                BlockTags.FEATURES_CANNOT_REPLACE,
                BlockTags.GEODE_INVALID_BLOCKS
        );

        GeodeBlockSettings build() {
            return new GeodeBlockSettings(
                    filling,
                    innerLayer,
                    alternativeInnerLayer,
                    middleLayer,
                    outerLayer,
                    innerPlacements,
                    cannotReplace,
                    invalidBlocks
            );
        }
    }

    public record GeodeLayers(
            @Info("The filling thickness, in the range [0.01, 50]")
            double filling,
            @Info("The inner layer thickness, in the range [0.01, 50]")
            double innerLayer,
            @Info("The middles later thickness, in the range [0.01, 50]")
            double middleLayer,
            @Info("Theouter layer thickness, in the range [0.01, 50] ")
            double outerLayer
    ) {
        public static final GeodeLayers DEFAULT = new GeodeLayers(1.7D, 2.2D, 3.2D, 4.2D);

        GeodeLayerSettings build() {
            return new GeodeLayerSettings(
                    filling,
                    innerLayer,
                    middleLayer,
                    outerLayer
            );
        }
    }

    public record GeodeCrack(
            @Info("The probability of generating a crack, in the range [0, 1]")
            double generateChance,
            @Info("The base size of the crack, in the range [0, 5]")
            double baseSize,
            @Info("The offset applied to the crack, in the range [0, 10]")
            int pointOffset
    ) {
        public static final GeodeCrack DEFAULT = new GeodeCrack(1.0D, 2.0D, 2);

        GeodeCrackSettings build() {
            return new GeodeCrackSettings(
                    generateChance,
                    baseSize,
                    pointOffset
            );
        }
    }
}
