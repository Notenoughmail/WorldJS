package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
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

    public ProbabilityFeatureConfigurationBuilder probability(float probability) {
        this.probability = assertUnit(probability, "probability");
        return this;
    }

    @Override
    protected ProbabilityFeatureConfiguration createFeatureConfiguration() {
        return new ProbabilityFeatureConfiguration(probability);
    }
}
