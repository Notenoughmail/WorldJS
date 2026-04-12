package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.CountConfiguration;

@ReturnsSelf
public class CountConfigurationBuilder extends ConfiguredFeatureBuilder<CountConfiguration> {

    public transient IntProvider count;

    public CountConfigurationBuilder(ResourceLocation id) {
        super(id);
    }

    @Info("The number of times to place, in the range [0, 256]")
    public CountConfigurationBuilder count(IntProvider count) {
        this.count = assertRange(count, 0, 256, "count");
        return this;
    }

    @Override
    protected CountConfiguration createFeatureConfiguration() {
        return new CountConfiguration(
                notNull(count, "count")
        );
    }

    @Override
    protected Feature<CountConfiguration> getFeature() {
        return Feature.SEA_PICKLE; // Yeah...
    }
}
