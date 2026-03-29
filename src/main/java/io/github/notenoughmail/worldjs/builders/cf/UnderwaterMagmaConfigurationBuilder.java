package io.github.notenoughmail.worldjs.builders.cf;

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

    public UnderwaterMagmaConfigurationBuilder floorSearchRange(int range) {
        floorSearchRange = assertRange(range, 0, 512, "Floor search range must be in range [0, 512]");
        return this;
    }

    public UnderwaterMagmaConfigurationBuilder placementRadiusAroundFloor(int radius) {
        placementRadiusAroundFloor = assertRange(radius, 0, 64, "Placement radius around floor must be in range [0, 64]");
        return this;
    }

    public UnderwaterMagmaConfigurationBuilder placementProbabilityPerValidPosition(float probability) {
        placementProbabilityPerValidPosition = assertUnit(probability, "Placement probability per valid position must be in range [0, 1]");
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
