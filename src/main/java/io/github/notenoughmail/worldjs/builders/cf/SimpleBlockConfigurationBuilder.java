package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

@ReturnsSelf
public class SimpleBlockConfigurationBuilder extends ConfiguredFeatureBuilder<SimpleBlockConfiguration> {

    public transient BlockStateProvider toPlace;

    public SimpleBlockConfigurationBuilder(ResourceLocation id) {
        super(id);
    }

    public SimpleBlockConfigurationBuilder toPlace(BlockStateProvider provider) {
        toPlace = provider;
        return this;
    }

    @Override
    protected SimpleBlockConfiguration createFeatureConfiguration() {
        return new SimpleBlockConfiguration(notNull(toPlace, "To place must be defined"));
    }

    @Override
    protected Feature<SimpleBlockConfiguration> getFeature() {
        return Feature.SIMPLE_BLOCK;
    }
}
