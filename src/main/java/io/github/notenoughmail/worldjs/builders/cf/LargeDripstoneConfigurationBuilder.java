package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import io.github.notenoughmail.worldjs.util.Validations;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.LargeDripstoneConfiguration;

@ReturnsSelf
public class LargeDripstoneConfigurationBuilder extends ConfiguredFeatureBuilder<LargeDripstoneConfiguration> {

    public transient int floorToCeilingSearchRange,
            minRadiusForWind;
    public transient IntProvider columnRadius;
    public transient FloatProvider heightScale,
            stalactiteBluntness,
            stalagmiteBluntness,
            windSpeed;
    public transient float maxColumnRadiusToCaveHeightRatio,
            minBluntnessFroWind;

    public LargeDripstoneConfigurationBuilder(ResourceLocation id) {
        super(id);
        floorToCeilingSearchRange = 30;
        maxColumnRadiusToCaveHeightRatio = 0.1F;
    }

    @Info("The search range from start point to cave floor or ceiling, in the range [1, 512]")
    public LargeDripstoneConfigurationBuilder floorToCeilingSearchRange(int range) {
        floorToCeilingSearchRange = Validations.assertRange(range, 1, 512, "floorToCeilingSearchRange");
        return this;
    }

    @Info("The min and max radius of the column, in the range [1, 60]")
    public LargeDripstoneConfigurationBuilder columnRadius(IntProvider provider) {
        columnRadius = Validations.assertRange(provider, 1, 60, "columnRadius");
        return this;
    }

    @Info("The height scale, a higher scale means a higher height. In the range [0, 20]")
    public LargeDripstoneConfigurationBuilder heightScale(FloatProvider provider) {
        heightScale = Validations.assertRange(provider, 0F, 20F, "heightScale");
        return this;
    }

    @Info("The maximum ratio between the column radius and cave height, in the range [0.1, 1]")
    public LargeDripstoneConfigurationBuilder maxColumnRadiusToCaveHeightRatio(float ratio) {
        maxColumnRadiusToCaveHeightRatio = Validations.assertRange(ratio, 0.1F, 1F, "maxColumnRadiusToCaveHeightRatio");
        return this;
    }

    @Info("The bluntness/truncation of stalactites, higher values leads to shorter height. In the range [0.1, 10]")
    public LargeDripstoneConfigurationBuilder stalactiteBluntness(FloatProvider provider) {
        stalactiteBluntness = Validations.assertRange(provider, 0.1F, 10F, "stalactiteBluntness");
        return this;
    }

    @Info("The bluntness/truncation of stalagmites, higher values leads to shorter height. In the range [0.1, 10]")
    public LargeDripstoneConfigurationBuilder stalagmiteBluntness(FloatProvider provider) {
        stalagmiteBluntness = Validations.assertRange(provider, 0.1F, 10F, "stalagmiteBluntness");
        return this;
    }

    @Info("The inclination of the feature, in the range [0, 2]")
    public LargeDripstoneConfigurationBuilder windSpeed(FloatProvider provider) {
        windSpeed = Validations.assertRange(provider, 0F, 2F, "windSpeed");
        return this;
    }

    @Info("The minimum column radius for wind to cause an inclination, in the range [0, 100]")
    public LargeDripstoneConfigurationBuilder minRadiusForWind(int radius) {
        minRadiusForWind = Validations.assertRange(radius, 0, 100, "minRadiusForWind");
        return this;
    }

    @Info("The minimum bluntness values for wind to cause an inclination, in the range [0, 5]")
    public LargeDripstoneConfigurationBuilder minBluntnessForWind(float bluntness) {
        minBluntnessFroWind = Validations.assertRange(bluntness, 0F, 5F, "minBluntnessForWind");
        return this;
    }

    @Override
    protected LargeDripstoneConfiguration createFeatureConfiguration() {
        return new LargeDripstoneConfiguration(
                floorToCeilingSearchRange,
                notNull(columnRadius, "columnRadius"),
                notNull(heightScale, "heightScale"),
                maxColumnRadiusToCaveHeightRatio,
                notNull(stalactiteBluntness, "stalactiteBluntness"),
                notNull(stalagmiteBluntness, "stalagmiteBluntness"),
                notNull(windSpeed, "windSpeed"),
                minRadiusForWind,
                minBluntnessFroWind
        );
    }

    @Override
    protected Feature<LargeDripstoneConfiguration> getFeature() {
        return Feature.LARGE_DRIPSTONE;
    }
}
