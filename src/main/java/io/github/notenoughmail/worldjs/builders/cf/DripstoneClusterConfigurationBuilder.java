package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.DripstoneClusterConfiguration;

@ReturnsSelf
public class DripstoneClusterConfigurationBuilder extends ConfiguredFeatureBuilder<DripstoneClusterConfiguration> {

    public transient int floorToCeilingSearchRange,
            maxStalHeightDiff,
            heightDeviation,
            maxDistFromEdgeAffectingChanceOfDripstoneColumn,
            maxDistFromCenterAffectingHeightBias;
    public transient IntProvider height,
            radius,
            dripstoneBlockLayerThickness;
    public transient FloatProvider density, wetness;
    public transient float chanceOfDripstoneColumnAtMaxDistanceFromCenter;

    public DripstoneClusterConfigurationBuilder(ResourceLocation id) {
        super(id);
        maxStalHeightDiff = heightDeviation = maxDistFromCenterAffectingHeightBias = maxDistFromEdgeAffectingChanceOfDripstoneColumn = 1;
    }

    public DripstoneClusterConfigurationBuilder floorToCeilingSearchRange(int range) {
        floorToCeilingSearchRange = assertRange(range, 1, 512, "floorToCeilingSearchRange");
        return this;
    }

    public DripstoneClusterConfigurationBuilder height(IntProvider provider) {
        height = assertRange(provider, 1, 128, "height");
        return this;
    }

    public DripstoneClusterConfigurationBuilder radius(IntProvider provider) {
        radius = assertRange(provider, 1, 128, "radius");
        return this;
    }

    public DripstoneClusterConfigurationBuilder maxStalagmiteStalactiteHeightDiff(int diff) {
        maxStalHeightDiff = assertRange(diff, 0, 64, "maxStalagmiteStalactiteHeightDiff");
        return this;
    }

    public DripstoneClusterConfigurationBuilder heightDeviation(int deviation) {
        heightDeviation = assertRange(deviation, 1, 64, "heightDeviation");
        return this;
    }

    public DripstoneClusterConfigurationBuilder dripstoneBlockLayerThickness(IntProvider provider) {
        dripstoneBlockLayerThickness = assertRange(provider, 0, 128, "dripstoneBlockLayerThickness");
        return this;
    }

    public DripstoneClusterConfigurationBuilder density(FloatProvider provider) {
        density = assertRange(provider, 0F, 2F, "density");
        return this;
    }

    public DripstoneClusterConfigurationBuilder wetness(FloatProvider provider) {
        wetness = assertRange(provider, 0F, 2F, "wetness");
        return this;
    }

    public DripstoneClusterConfigurationBuilder chanceOfDripstoneColumnAtMaxDistanceFromCenter(float chance) {
        chanceOfDripstoneColumnAtMaxDistanceFromCenter = assertUnit(chance, "chanceOfDripstoneColumnAtMaxDistanceFromCenter");
        return this;
    }

    public DripstoneClusterConfigurationBuilder maxDistanceFromEdgeAffectingChanceOfDripstoneColumn(int dist) {
        maxDistFromEdgeAffectingChanceOfDripstoneColumn = assertRange(dist, 1, 64, "maxDistanceFromEdgeAffectingChanceOfDripstoneColumn");
        return this;
    }

    public DripstoneClusterConfigurationBuilder maxDistanceFromCenterAffectingHeightBias(int dist) {
        maxDistFromCenterAffectingHeightBias = assertRange(dist, 1, 64, "maxDistanceFromCenterAffectingHeightBias");
        return this;
    }

    @Override
    protected DripstoneClusterConfiguration createFeatureConfiguration() {
        return new DripstoneClusterConfiguration(
                floorToCeilingSearchRange,
                notNull(height, "height"),
                notNull(radius, "radius"),
                maxStalHeightDiff,
                heightDeviation,
                notNull(dripstoneBlockLayerThickness, "dripstoneBlockLayerThickness"),
                notNull(density, "density"),
                notNull(wetness, "wetness"),
                chanceOfDripstoneColumnAtMaxDistanceFromCenter,
                maxDistFromEdgeAffectingChanceOfDripstoneColumn,
                maxDistFromCenterAffectingHeightBias
        );
    }

    @Override
    protected Feature<DripstoneClusterConfiguration> getFeature() {
        return Feature.DRIPSTONE_CLUSTER;
    }
}
