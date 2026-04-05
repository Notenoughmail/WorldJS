package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.IntProvider;
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
    }

    public GeodeConfigurationBuilder blocks(
            BlockStateProvider fillingProvider,
            BlockStateProvider innerLayerProvider,
            BlockStateProvider alternativeInnerLayerProvider,
            BlockStateProvider middleLayerProvider,
            BlockStateProvider outerLayerProvider,
            List<BlockState> innerPlacements,
            TagKey<Block> cannotReplace,
            TagKey<Block> invalidBlocks
    ) {
        if (innerPlacements.isEmpty()) throw new IllegalArgumentException("Must have at least one inner placement!");
        blocks = new GeodeBlockSettings(
                fillingProvider,
                innerLayerProvider,
                alternativeInnerLayerProvider,
                middleLayerProvider,
                outerLayerProvider,
                innerPlacements,
                cannotReplace,
                invalidBlocks
        );
        return this;
    }

    public GeodeConfigurationBuilder layers(Consumer<Layer> layer) {
        layer.accept(new Layer());
        return this;
    }

    public GeodeConfigurationBuilder crack(Consumer<Crack> crack) {
        crack.accept(new Crack());
        return this;
    }

    public GeodeConfigurationBuilder usePotentialPlacementsChance(double chance) {
        usePotentialPlacementsChance = assertUnit(chance, "usePotentialPlacementsChance");
        return this;
    }

    public GeodeConfigurationBuilder useAlternativeLayer0Chance(double chance) {
        useAlternativeLayer0Chance = assertUnit(chance, "useAlternativeLayer0Chance");
        return this;
    }

    public GeodeConfigurationBuilder outerWallDistance(IntProvider provider) {
        outWallDistance = assertRange(provider, 1, 20, "outerWallDistance");
        return this;
    }

    public GeodeConfigurationBuilder distributionPoints(IntProvider provider) {
        distributionPoints = assertRange(provider, 1, 20, "distributionPoints");
        return this;
    }

    public GeodeConfigurationBuilder pointOffset(IntProvider provider) {
        pointOffset = assertRange(provider, 0, 10, "pointOffset");
        return this;
    }

    public GeodeConfigurationBuilder minGenOffset(int offset) {
        minGenOffset = offset;
        return this;
    }

    public GeodeConfigurationBuilder maxGenOffset(int offset) {
        maxGenOffset = offset;
        return this;
    }

    public GeodeConfigurationBuilder noiseMultiplier(double multiplier) {
        noiseMultiplier = assertUnit(multiplier, "noiseMultiplier");
        return this;
    }

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
                notNull(outWallDistance, "outerWallDistance"),
                notNull(distributionPoints, "distributionPoints"),
                notNull(pointOffset, "pointOffset"),
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

        public Crack generateCrackChance(double chance) {
            crackGenerateCrackChance = assertUnit(chance, "generateCrackChance");
            return this;
        }

        public Crack baseCrackSize(double size) {
            crackBaseCrackSize = assertRange(size, 0D, 5D, "baseCrackSize");
            return this;
        }

        public Crack crackPointOffset(int offset) {
            crackCrackPointOffset = assertRange(offset, 0, 10, "crackPointOffset");
            return this;
        }
    }

    public class Layer {

        public Layer filling(double filling) {
            layerFilling = f(filling, "filling");
            return this;
        }

        public Layer innerLayer(double layer) {
            layerInnerLayer = f(layer, "innerLayer");
            return this;
        }

        public Layer middleLayer(double layer) {
            layerMiddleLayer = f(layer, "middleLayer");
            return this;
        }

        public Layer outerLayer(double layer) {
            layerOuterLayer = f(layer, "outerLayer");
            return this;
        }

        private static double f(double val, String name) {
            return assertRange(val, 0.01D, 50D, name);
        }
    }
}
