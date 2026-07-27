package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import io.github.notenoughmail.worldjs.util.ServerRegistryHolderSet;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleRandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

@ReturnsSelf
public class SimpleRandomFeatureConfigurationBuilder extends ConfiguredFeatureBuilder<SimpleRandomFeatureConfiguration> {

    public transient HolderSet<PlacedFeature> features;

    public SimpleRandomFeatureConfigurationBuilder(ResourceLocation id) {
        super(id);
    }

    @Info("The placed features to randomly select from to place")
    public SimpleRandomFeatureConfigurationBuilder features(ServerRegistryHolderSet<PlacedFeature> features) {
        this.features = features.convertWithValidation("features", this::exception);
        return this;
    }

    @Override
    protected SimpleRandomFeatureConfiguration createFeatureConfiguration() {
        return new SimpleRandomFeatureConfiguration(
                notNull(features, "features")
        );
    }

    @Override
    protected Feature<SimpleRandomFeatureConfiguration> getFeature() {
        return Feature.SIMPLE_RANDOM_SELECTOR;
    }
}
