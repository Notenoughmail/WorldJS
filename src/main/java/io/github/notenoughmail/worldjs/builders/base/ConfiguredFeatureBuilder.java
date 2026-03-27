package io.github.notenoughmail.worldjs.builders.base;

import dev.latvian.mods.kubejs.registry.AdditionalObjectRegistry;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.BuilderFactory;
import dev.latvian.mods.kubejs.util.KubeResourceLocation;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ReturnsSelf
public abstract class ConfiguredFeatureBuilder<FC extends FeatureConfiguration, F extends Feature<FC>> extends BuilderBase<ConfiguredFeature<FC, F>> {

    protected static void assertPositive(int val, String msg) {
        if (val < 1) throw new IllegalArgumentException(msg);
    }

    protected static void assertNonNegative(int val, String msg) {
        if (val < 0) throw new IllegalArgumentException(msg);
    }

    protected static void assertRange(int val, int min, int max, String msg) {
        if (val < min || val > max) throw new IllegalArgumentException(msg);
    }

    protected static void assertUnit(float val, String msg) {
        if (val < 0f || val > 1f) throw new IllegalArgumentException(msg);
    }

    protected static void assertRange(IntProvider provider, int min, int max, String msg) {
        if (provider.getMinValue() < min || provider.getMaxValue() > max) throw new IllegalArgumentException(msg);
    }

    protected static <T> T notNull(T t, String msg) {
        return Objects.requireNonNull(t, msg);
    }

    public transient PlacedFeatureBuilder placedFeature;

    public ConfiguredFeatureBuilder(ResourceLocation id) {
        super(id);
    }

    public ConfiguredFeatureBuilder<FC, F> withPlacement(Consumer<PlacedFeatureBuilder> builder) {
        return withPlacement(KubeResourceLocation.wrap(id), builder);
    }

    public ConfiguredFeatureBuilder<FC, F> withPlacement(KubeResourceLocation id, Consumer<PlacedFeatureBuilder> builder) {
        placedFeature = new PlacedFeatureBuilder(id.wrapped());
        builder.accept(placedFeature);
        placedFeature.configuredFeature(this);
        return this;
    }

    abstract protected FC createFeatureConfiguration();

    abstract protected F getFeature();

    @Override
    public void createAdditionalObjects(AdditionalObjectRegistry registry) {
        if (placedFeature != null) {
            registry.add(Registries.PLACED_FEATURE, placedFeature);
        }
    }

    @Override
    public ConfiguredFeature<FC, F> createObject() {
        return new ConfiguredFeature<>(
                getFeature(),
                createFeatureConfiguration()
        );
    }

    public final static class NoneConfig<F extends Feature<NoneFeatureConfiguration>> extends ConfiguredFeatureBuilder<NoneFeatureConfiguration, F> {

        public static <F extends Feature<NoneFeatureConfiguration>> BuilderFactory factory(F feature) {
            return supplierFactory(() -> feature);
        }

        public static <F extends Feature<NoneFeatureConfiguration>> BuilderFactory supplierFactory(Supplier<F> feature) {
            return i -> new NoneConfig<>(i, feature);
        }

        public transient final Supplier<F> feature;

        public NoneConfig(ResourceLocation id, Supplier<F> feature) {
            super(id);
            this.feature = feature;
        }

        @Override
        protected NoneFeatureConfiguration createFeatureConfiguration() {
            return FeatureConfiguration.NONE;
        }

        @Override
        protected F getFeature() {
            return feature.get();
        }
    }
}
