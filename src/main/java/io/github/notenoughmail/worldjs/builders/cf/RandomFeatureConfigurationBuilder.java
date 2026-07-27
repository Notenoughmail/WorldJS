package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import io.github.notenoughmail.worldjs.util.Validations;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.ArrayList;
import java.util.List;

@ReturnsSelf
public class RandomFeatureConfigurationBuilder extends ConfiguredFeatureBuilder<RandomFeatureConfiguration> {

    public transient List<WeightedPlacedFeature> features;
    public transient Holder.Reference<PlacedFeature> fallback;

    public RandomFeatureConfigurationBuilder(ResourceLocation id) {
        super(id);
        features = new ArrayList<>();
    }

    @Info(
            value = "Adds a placed feature to randomly choose from",
            params = {
                    @Param(name = "feature", value = "The feature to place"),
                    @Param(name = "chance", value = "The chance of the feature being chosen, in the range [0, 1]")
            }
    )
    public RandomFeatureConfigurationBuilder withFeature(Holder.Reference<PlacedFeature> feature, float chance) {
        features.add(new WeightedPlacedFeature(
                feature,
                Validations.assertUnit(chance, "chance")
        ));
        return this;
    }

    @Info("The feature to place if none are chosen")
    public RandomFeatureConfigurationBuilder defaultFeature(Holder.Reference<PlacedFeature> feature) {
        fallback = feature;
        return this;
    }

    @Override
    protected RandomFeatureConfiguration createFeatureConfiguration() {
        return new RandomFeatureConfiguration(
                features,
                notNull(fallback, "defaultFeature")
        );
    }

    @Override
    protected Feature<RandomFeatureConfiguration> getFeature() {
        return Feature.RANDOM_SELECTOR;
    }
}
