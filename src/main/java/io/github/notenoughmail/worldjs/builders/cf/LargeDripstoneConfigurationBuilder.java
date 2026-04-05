package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
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

    public LargeDripstoneConfigurationBuilder floorToCeilingSearchRange(int range) {
        floorToCeilingSearchRange = assertRange(range, 1, 512, "floorToCeilingSearchRange");
        return this;
    }

    public LargeDripstoneConfigurationBuilder columnRadius(IntProvider provider) {
        columnRadius = provider;
        return this;
    }

    public LargeDripstoneConfigurationBuilder heightScale(FloatProvider provider) {
        heightScale = assertRange(provider, 0F, 20F, "heightScale");
        return this;
    }

    public LargeDripstoneConfigurationBuilder maxColumnRadiusToCaveHeightRatio(float ratio) {
        maxColumnRadiusToCaveHeightRatio = assertRange(ratio, 0.1F, 1F, "maxCoumnRadiusToCaveHeightRatio");
        return this;
    }

    public LargeDripstoneConfigurationBuilder stalactiteBluntness(FloatProvider provider) {
        stalactiteBluntness = assertRange(provider, 0.1F, 10F, "stalactiteBluntness");
        return this;
    }

    public LargeDripstoneConfigurationBuilder stalagmiteBluntness(FloatProvider provider) {
        stalagmiteBluntness = assertRange(provider, 0.1F, 10F, "stalagmiteBluntness");
        return this;
    }

    public LargeDripstoneConfigurationBuilder windSpeed(FloatProvider provider) {
        windSpeed = assertRange(provider, 0F, 2F, "windSpeed");
        return this;
    }

    public LargeDripstoneConfigurationBuilder minRadiusForWind(int radius) {
        minRadiusForWind = assertRange(radius, 0, 100, "minRadiusForWind");
        return this;
    }

    public LargeDripstoneConfigurationBuilder minBluntnessForWind(float bluntness) {
        minBluntnessFroWind = assertRange(bluntness, 0F, 5F, "minBluntnessForWind");
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
