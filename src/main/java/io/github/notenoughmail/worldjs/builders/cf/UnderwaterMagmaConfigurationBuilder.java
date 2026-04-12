package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.UnderwaterMagmaConfiguration;

@ReturnsSelf
public class UnderwaterMagmaConfigurationBuilder extends ConfiguredFeatureBuilder<UnderwaterMagmaConfiguration> {

    public transient int floorSearchRange, placementRadiusAroundFloor;
    public transient float placementProbabilityPerValidPosition;

    public UnderwaterMagmaConfigurationBuilder(ResourceLocation id) {
        super(id);
    }

    @Info("The maximum distance to search for a placement position, in the range [0, 512]")
    public UnderwaterMagmaConfigurationBuilder floorSearchRange(int range) {
        floorSearchRange = assertRange(range, 0, 512, "floorSearchRange");
        return this;
    }

    @Info("The radius around the selected position to potentially place magma, in the range [0, 64]")
    public UnderwaterMagmaConfigurationBuilder placementRadiusAroundFloor(int radius) {
        placementRadiusAroundFloor = assertRange(radius, 0, 64, "placementRadiusAroundFloor");
        return this;
    }

    @Info("The probability of a magma block being placed at a position, in the range [0, 1]")
    public UnderwaterMagmaConfigurationBuilder placementProbabilityPerValidPosition(float probability) {
        placementProbabilityPerValidPosition = assertUnit(probability, "placementProbabilityPerValidPosition");
        return this;
    }

    @Override
    protected UnderwaterMagmaConfiguration createFeatureConfiguration() {
        return new UnderwaterMagmaConfiguration(
                floorSearchRange,
                placementRadiusAroundFloor,
                placementProbabilityPerValidPosition
        );
    }

    @Override
    protected Feature<UnderwaterMagmaConfiguration> getFeature() {
        return Feature.UNDERWATER_MAGMA;
    }
}
