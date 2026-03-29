package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.function.Supplier;

@ReturnsSelf
public class RandomPatchConfigurationBuilder extends ConfiguredFeatureBuilder.WithFeature<RandomPatchConfiguration> {

    public transient int tries, xzSpread, ySpread;
    public transient Holder.Reference<PlacedFeature> feature;

    public RandomPatchConfigurationBuilder(ResourceLocation id, Supplier<Feature<RandomPatchConfiguration>> feature) {
        super(id, feature);
        tries = 128;
        xzSpread = 7;
        ySpread = 3;
    }

    public RandomPatchConfigurationBuilder tries(int tries) {
        this.tries = assertPositive(tries, "Tries must be positive");
        return this;
    }

    public RandomPatchConfigurationBuilder xzSpread(int spread) {
        xzSpread = assertNonNegative(spread, "X-z spread must be non negative");
        return this;
    }

    public RandomPatchConfigurationBuilder ySpread(int spread) {
        ySpread = assertNonNegative(spread, "Y spread must be non negative");
        return this;
    }

    public RandomPatchConfigurationBuilder spread(int xzSpread, int ySpread) {
        return xzSpread(xzSpread).ySpread(ySpread);
    }

    public RandomPatchConfigurationBuilder feature(Holder.Reference<PlacedFeature> placedFeature) {
        feature = placedFeature;
        return this;
    }

    @Override
    protected RandomPatchConfiguration createFeatureConfiguration() {
        return new RandomPatchConfiguration(
                tries,
                xzSpread,
                ySpread,
                notNull(feature, "Feature must be defined!")
        );
    }
}
