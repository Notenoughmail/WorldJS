package io.github.notenoughmail.worldjs.builders.base;

import dev.latvian.mods.kubejs.registry.AdditionalObjectRegistry;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.BuilderFactory;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.function.Supplier;

@ReturnsSelf
public abstract class ConfiguredFeatureBuilder<FC extends FeatureConfiguration, F extends Feature<FC>> extends BuilderBase<ConfiguredFeature<FC, F>> {

    public ConfiguredFeatureBuilder(ResourceLocation id) {
        super(id);
    }

    abstract protected FC createFeatureConfiguration();

    abstract protected F getFeature();

    @Override
    public void createAdditionalObjects(AdditionalObjectRegistry registry) {
        super.createAdditionalObjects(registry);
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
