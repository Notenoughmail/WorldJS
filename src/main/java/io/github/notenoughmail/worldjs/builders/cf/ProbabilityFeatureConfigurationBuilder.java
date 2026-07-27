package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import io.github.notenoughmail.worldjs.util.Validations;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;

import java.util.function.Supplier;

@ReturnsSelf
public class ProbabilityFeatureConfigurationBuilder extends ConfiguredFeatureBuilder.WithFeature<ProbabilityFeatureConfiguration> {

    public transient float probability;

    public ProbabilityFeatureConfigurationBuilder(ResourceLocation id, Supplier<Feature<ProbabilityFeatureConfiguration>> feature) {
        super(id, feature);
        probability = 1f;
    }

    @Info("The probability of special properties being placed")
    public ProbabilityFeatureConfigurationBuilder probability(float probability) {
        this.probability = Validations.assertUnit(probability, "probability");
        return this;
    }

    @Override
    protected ProbabilityFeatureConfiguration createFeatureConfiguration() {
        return new ProbabilityFeatureConfiguration(probability);
    }
}
