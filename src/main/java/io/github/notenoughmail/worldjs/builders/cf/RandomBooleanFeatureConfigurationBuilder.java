package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomBooleanFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

@ReturnsSelf
public class RandomBooleanFeatureConfigurationBuilder extends ConfiguredFeatureBuilder<RandomBooleanFeatureConfiguration> {

    public transient Holder.Reference<PlacedFeature> featureTrue, featureFalse;

    public RandomBooleanFeatureConfigurationBuilder(ResourceLocation id) {
        super(id);
    }

    @Info("The placed feature to place on a random `true` result")
    public RandomBooleanFeatureConfigurationBuilder featureTrue(Holder.Reference<PlacedFeature> feature) {
        featureTrue = feature;
        return this;
    }

    @Info("The placed feature to place on a random `false` result")
    public RandomBooleanFeatureConfigurationBuilder featureFalse(Holder.Reference<PlacedFeature> feature) {
        featureFalse = feature;
        return this;
    }

    @Override
    protected RandomBooleanFeatureConfiguration createFeatureConfiguration() {
        return new RandomBooleanFeatureConfiguration(
                notNull(featureTrue, "featureTrue"),
                notNull(featureFalse, "featureFalse")
        );
    }

    @Override
    protected Feature<RandomBooleanFeatureConfiguration> getFeature() {
        return Feature.RANDOM_BOOLEAN_SELECTOR;
    }
}
