package io.github.notenoughmail.worldjs.builders.base;

import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.registry.AdditionalObjectRegistry;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.BuilderFactory;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.KubeResourceLocation;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ReturnsSelf
public abstract class ConfiguredFeatureBuilder<FC extends FeatureConfiguration> extends BuilderBase<ConfiguredFeature<FC, Feature<FC>>> {

    public static <FC extends FeatureConfiguration> BuilderFactory factory(Feature<FC> feature, BiFunction<ResourceLocation, Supplier<Feature<FC>>, ? extends ConfiguredFeatureBuilder<FC>> factory) {
        return supplierFactory(() -> feature, factory);
    }

    public static <FC extends FeatureConfiguration> BuilderFactory supplierFactory(Supplier<Feature<FC>> feature, BiFunction<ResourceLocation, Supplier<Feature<FC>>, ? extends ConfiguredFeatureBuilder<FC>> factory) {
        return i -> factory.apply(i, feature);
    }

    protected static int assertPositive(int val, String msg) {
        if (val < 1) throw new IllegalArgumentException(msg);
        return val;
    }

    protected static int assertNonNegative(int val, String msg) {
        if (val < 0) throw new IllegalArgumentException(msg);
        return val;
    }

    protected static int assertRange(int val, int min, int max, String msg) {
        if (val < min || val > max) throw new IllegalArgumentException(msg);
        return val;
    }

    protected static float assertUnit(float val, String msg) {
        if (val < 0f || val > 1f) throw new IllegalArgumentException(msg);
        return val;
    }

    protected static IntProvider assertRange(IntProvider provider, int min, int max, String msg) {
        if (provider.getMinValue() < min || provider.getMaxValue() > max) throw new IllegalArgumentException(msg);
        return provider;
    }

    protected <T> T notNull(T t, String msg) {
        if (t == null) {
            throw new KubeRuntimeException(msg)
                    .source(sourceLine);
        }
        return t;
    }

    public transient PlacedFeatureBuilder placedFeature;

    public ConfiguredFeatureBuilder(ResourceLocation id) {
        super(id);
    }

    public ConfiguredFeatureBuilder<FC> withPlacement(Context ctx, Consumer<PlacedFeatureBuilder> builder) {
        return withPlacement(ctx, KubeResourceLocation.wrap(id), builder);
    }

    public ConfiguredFeatureBuilder<FC> withPlacement(Context ctx, KubeResourceLocation id, Consumer<PlacedFeatureBuilder> builder) {
        placedFeature = new PlacedFeatureBuilder(id.wrapped());
        placedFeature.sourceLine = SourceLine.of(ctx);
        builder.accept(placedFeature);
        placedFeature.configuredFeature(this);
        return this;
    }

    abstract protected FC createFeatureConfiguration();

    abstract protected Feature<FC> getFeature();

    @Override
    public void createAdditionalObjects(AdditionalObjectRegistry registry) {
        if (placedFeature != null) {
            registry.add(Registries.PLACED_FEATURE, placedFeature);
        }
    }

    @Override
    public ConfiguredFeature<FC, Feature<FC>> createObject() {
        return new ConfiguredFeature<>(
                getFeature(),
                createFeatureConfiguration()
        );
    }

    public final static class NoneConfig extends WithFeature<NoneFeatureConfiguration> {

        public NoneConfig(ResourceLocation id, Supplier<Feature<NoneFeatureConfiguration>> feature) {
            super(id, feature);
        }

        @Override
        protected NoneFeatureConfiguration createFeatureConfiguration() {
            return FeatureConfiguration.NONE;
        }
    }

    public abstract static class WithFeature<FC extends FeatureConfiguration> extends ConfiguredFeatureBuilder<FC> {

        private final Supplier<Feature<FC>> feature;

        public WithFeature(ResourceLocation id, Supplier<Feature<FC>> feature) {
            super(id);
            this.feature = feature;
        }

        @Override
        protected Feature<FC> getFeature() {
            return feature.get();
        }
    }
}
