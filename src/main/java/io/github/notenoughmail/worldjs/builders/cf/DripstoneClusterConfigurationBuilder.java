package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import io.github.notenoughmail.worldjs.util.Validations;
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
        floorToCeilingSearchRange
                = maxStalHeightDiff
                = heightDeviation
                = maxDistFromCenterAffectingHeightBias
                = maxDistFromEdgeAffectingChanceOfDripstoneColumn
                = 1;
    }

    @Info("How many blocks the feature searches floor-to-ceiling, in the range [1, 512]")
    public DripstoneClusterConfigurationBuilder floorToCeilingSearchRange(int range) {
        floorToCeilingSearchRange = Validations.assertRange(range, 1, 512, "floorToCeilingSearchRange");
        return this;
    }

    @Info("The height of the cluster, in the range [1, 128]")
    public DripstoneClusterConfigurationBuilder height(IntProvider provider) {
        height = Validations.assertRange(provider, 1, 128, "height");
        return this;
    }

    @Info("The radius of the cluster, in the range [1, 128]")
    public DripstoneClusterConfigurationBuilder radius(IntProvider provider) {
        radius = Validations.assertRange(provider, 1, 128, "radius");
        return this;
    }

    @Info("The maximum difference between the height of stalagmites and stalactites, in the range [0, 64]")
    public DripstoneClusterConfigurationBuilder maxStalagmiteStalactiteHeightDiff(int diff) {
        maxStalHeightDiff = Validations.assertRange(diff, 0, 64, "maxStalagmiteStalactiteHeightDiff");
        return this;
    }

    @Info("The maximum difference from the height of a dripstone")
    public DripstoneClusterConfigurationBuilder heightDeviation(int deviation) {
        heightDeviation = Validations.assertRange(deviation, 1, 64, "heightDeviation");
        return this;
    }

    @Info("The dripstone block layer's thickness, in the range [0, 128]")
    public DripstoneClusterConfigurationBuilder dripstoneBlockLayerThickness(IntProvider provider) {
        dripstoneBlockLayerThickness = Validations.assertRange(provider, 0, 128, "dripstoneBlockLayerThickness");
        return this;
    }

    @Info("The density of columns, in the range [0, 2]")
    public DripstoneClusterConfigurationBuilder density(FloatProvider provider) {
        density = Validations.assertRange(provider, 0F, 2F, "density");
        return this;
    }

    @Info("The chance of also placing a pool, in the range [0, 2]")
    public DripstoneClusterConfigurationBuilder wetness(FloatProvider provider) {
        wetness = Validations.assertRange(provider, 0F, 2F, "wetness");
        return this;
    }

    @Info("The chance there is a column at the maximum distance from the center, in the range [0, 1]")
    public DripstoneClusterConfigurationBuilder chanceOfDripstoneColumnAtMaxDistanceFromCenter(float chance) {
        chanceOfDripstoneColumnAtMaxDistanceFromCenter = Validations.assertUnit(chance, "chanceOfDripstoneColumnAtMaxDistanceFromCenter");
        return this;
    }

    @Info("The maximum distance from edge that can affect the chance of a dripstone column placing, in the range [1, 64]")
    public DripstoneClusterConfigurationBuilder maxDistanceFromEdgeAffectingChanceOfDripstoneColumn(int dist) {
        maxDistFromEdgeAffectingChanceOfDripstoneColumn = Validations.assertRange(dist, 1, 64, "maxDistanceFromEdgeAffectingChanceOfDripstoneColumn");
        return this;
    }

    @Info("The maximum distance from the center that can affect the height bias, in the range [1, 64]")
    public DripstoneClusterConfigurationBuilder maxDistanceFromCenterAffectingHeightBias(int dist) {
        maxDistFromCenterAffectingHeightBias = Validations.assertRange(dist, 1, 64, "maxDistanceFromCenterAffectingHeightBias");
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
