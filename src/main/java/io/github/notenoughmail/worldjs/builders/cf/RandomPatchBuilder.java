package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.registry.BuilderFactory;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.function.Supplier;

public abstract class RandomPatchBuilder<F extends Feature<RandomPatchConfiguration>> extends ConfiguredFeatureBuilder<RandomPatchConfiguration, F> {

    public static <F extends Feature<RandomPatchConfiguration>> BuilderFactory factory(F feature) {
        return supplierFactory(() -> feature);
    }

    public static <F extends Feature<RandomPatchConfiguration>> BuilderFactory supplierFactory(Supplier<F> source) {
        return i -> new RandomPatchBuilder<F>(i) {
            @Override
            protected F getFeature() {
                return source.get();
            }
        };
    }

    public transient int tries, xzSpread, ySpread;
    public transient Holder.Reference<PlacedFeature> feature;

    public RandomPatchBuilder(ResourceLocation id) {
        super(id);
        tries = 128;
        xzSpread = 7;
        ySpread = 3;
    }

    public RandomPatchBuilder<F> tries(int tries) {
        assertPositive(tries, "Tries must be positive");
        this.tries = tries;
        return this;
    }

    public RandomPatchBuilder<F> xzSpread(int spread) {
        assertNonNegative(spread, "X-z spread must be non negative");
        xzSpread = spread;
        return this;
    }

    public RandomPatchBuilder<F> ySpread(int spread) {
        assertNonNegative(spread, "Y spread must be non negative");
        ySpread = spread;
        return this;
    }

    public RandomPatchBuilder<F> spread(int xzSpread, int ySpread) {
        return xzSpread(xzSpread).ySpread(ySpread);
    }

    public RandomPatchBuilder<F> feature(Holder.Reference<PlacedFeature> placedFeature) {
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
