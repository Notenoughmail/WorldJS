package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.List;
import java.util.function.Consumer;

@ReturnsSelf
public class GeodeConfigurationBuilder extends ConfiguredFeatureBuilder<GeodeConfiguration> {

    public transient double usePotentialPlacementsChance,
            useAlternativeLayer0Chance,
            noiseMultiplier,
            layerFilling,
            layerInnerLayer,
            layerMiddleLayer,
            layerOuterLayer,
            crackGenerateCrackChance,
            crackBaseCrackSize;
    public transient boolean placementsRequireLayer0Alternative;
    public transient int minGenOffset, maxGenOffset, invalidBlocksThreshold, crackCrackPointOffset;
    public transient IntProvider outWallDistance, distributionPoints, pointOffset;
    public transient GeodeBlockSettings blocks;

    public GeodeConfigurationBuilder(ResourceLocation id) {
        super(id);
        usePotentialPlacementsChance = 0.35D;
        useAlternativeLayer0Chance = 0D;
        noiseMultiplier = 0.05D;
        placementsRequireLayer0Alternative = true;
        minGenOffset = -16;
        maxGenOffset = 16;
        layerFilling = 1.7D;
        layerInnerLayer = 2.2D;
        layerMiddleLayer = 3.2D;
        layerOuterLayer = 4.2D;
        crackGenerateCrackChance = 1.0D;
        crackBaseCrackSize = 2.0D;
        crackCrackPointOffset = 2;
        outWallDistance = UniformInt.of(4, 5);
        distributionPoints = UniformInt.of(3, 4);
        pointOffset = UniformInt.of(1, 2);
    }

    @Info("The blocks used for the geode")
    public GeodeConfigurationBuilder blocks(Blocks blocks) {
        this.blocks = blocks.build();
        return this;
    }

    @Info("The thickness of each layer")
    public GeodeConfigurationBuilder layers(Consumer<Layer> layer) {
        layer.accept(new Layer());
        return this;
    }

    @Info("The crack properties")
    public GeodeConfigurationBuilder crack(Consumer<Crack> crack) {
        crack.accept(new Crack());
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
                notNull(blocks, "blocks"),
                new GeodeLayerSettings(
                        layerFilling,
                        layerInnerLayer,
                        layerMiddleLayer,
                        layerOuterLayer
                ),
                new GeodeCrackSettings(
                        crackGenerateCrackChance,
                        crackBaseCrackSize,
                        crackCrackPointOffset
                ),
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

    public class Crack {

        @Info("The probability of generating a crack, in the range [0, 1]. Defaults to 1")
        public Crack generateCrackChance(double chance) {
            crackGenerateCrackChance = assertUnit(chance, "generateCrackChance");
            return this;
        }

        @Info("The base size of the crack, in the range [0, 5]. Defaults to 2")
        public Crack baseCrackSize(double size) {
            crackBaseCrackSize = assertRange(size, 0D, 5D, "baseCrackSize");
            return this;
        }

        @Info("The offset applied to the crack, in the range [0, 10]. Defaults to 2")
        public Crack crackPointOffset(int offset) {
            crackCrackPointOffset = assertRange(offset, 0, 10, "crackPointOffset");
            return this;
        }
    }

    public class Layer {

        @Info("The filling thickness, in the range [0.01, 50]. Defaults to 1.7")
        public Layer filling(double filling) {
            layerFilling = f(filling, "filling");
            return this;
        }

        @Info("The inner layer thickness, in the range [0.01, 50]. Defaults to 2.2")
        public Layer innerLayer(double layer) {
            layerInnerLayer = f(layer, "innerLayer");
            return this;
        }

        @Info("The middle layer thickness, in the range [0.01, 50]. Defaults to 3.2")
        public Layer middleLayer(double layer) {
            layerMiddleLayer = f(layer, "middleLayer");
            return this;
        }

        @Info("The outer layer thickness, in the range [0.01, 50]. Defaults to 4.2")
        public Layer outerLayer(double layer) {
            layerOuterLayer = f(layer, "outerLayer");
            return this;
        }

        private static double f(double val, String name) {
            return assertRange(val, 0.01D, 50D, name);
        }
    }

    public record Blocks(
            @Info("The blocks of the 'filling' layer, air by default")
            BlockStateProvider fillingProvider,
            @Info("The blocks of the inner layer, amethyst by default")
            BlockStateProvider innerLayerProvider,
            @Info("The alternative blocks of the inner layer, budding amethyst by default")
            BlockStateProvider alternativeInnerLayerProvider,
            @Info("The blocks of the middle layer, calcite by default")
            BlockStateProvider middleLayerProvider,
            @Info("The blocks of the outer layer, smooth basalt by default")
            BlockStateProvider outerLayerProvider,
            @Info("The blocks to place adjacent to the alternative inner layer blocks, amethyst buds and clusters by default")
            List<BlockState> innerPlacements,
            @Info("The blocks that the geode cannot replace, defaults to `minecraft:features_cannot_replace`")
            TagKey<Block> cannotReplace,
            @Info("Blocks which the geode considers invalid. Is not respected due to MC-264886. Defaults to `minecraft:geode_invalid_blocks`")
            TagKey<Block> invalidBlocks
    ) {
        GeodeBlockSettings build() {
            if (innerPlacements.isEmpty()) throw new IllegalArgumentException("Must have at least one inner placement!");
            return new GeodeBlockSettings(
                    fillingProvider,
                    innerLayerProvider,
                    alternativeInnerLayerProvider,
                    middleLayerProvider,
                    outerLayerProvider,
                    innerPlacements,
                    cannotReplace,
                    invalidBlocks
            );
        }
    }
}
