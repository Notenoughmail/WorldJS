package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.ColumnFeatureConfiguration;

@ReturnsSelf
public class ColumnFeatureConfigurationBuilder extends ConfiguredFeatureBuilder<ColumnFeatureConfiguration> {

    public transient IntProvider reach, height;

    public ColumnFeatureConfigurationBuilder(ResourceLocation id) {
        super(id);
    }

    @Info("The max radius of a column, in the range [0, 3]")
    public ColumnFeatureConfigurationBuilder reach(IntProvider provider) {
        reach = assertRange(provider, 0, 3, "reach");
        return this;
    }

    @Info("The maximum height, in the range [1, 10]")
    public ColumnFeatureConfigurationBuilder height(IntProvider provider) {
        height = assertRange(provider, 1, 10, "height");
        return this;
    }

    @Override
    protected ColumnFeatureConfiguration createFeatureConfiguration() {
        return new ColumnFeatureConfiguration(
                notNull(reach, "reach"),
                notNull(height, "height")
        );
    }

    @Override
    protected Feature<ColumnFeatureConfiguration> getFeature() {
        return Feature.BASALT_COLUMNS;
    }
}
