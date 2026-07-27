package io.github.notenoughmail.worldjs.builders.base;

import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.registry.AdditionalObjectRegistry;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.BuilderFactory;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.kubejs.util.KubeResourceLocation;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.util.Validations;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@ReturnsSelf
public abstract class ConfiguredFeatureBuilder<FC extends FeatureConfiguration> extends BuilderBase<ConfiguredFeature<FC, Feature<FC>>> {

    public static <FC extends FeatureConfiguration> BuilderFactory factory(Feature<FC> feature, BiFunction<ResourceLocation, Supplier<Feature<FC>>, ? extends ConfiguredFeatureBuilder<FC>> factory) {
        return supplierFactory(() -> feature, factory);
    }

    public static <FC extends FeatureConfiguration> BuilderFactory supplierFactory(Supplier<Feature<FC>> feature, BiFunction<ResourceLocation, Supplier<Feature<FC>>, ? extends ConfiguredFeatureBuilder<FC>> factory) {
        return i -> factory.apply(i, feature);
    }

    @Deprecated(forRemoval = true)
    protected static int assertPositive(int val, String name) {
        return Validations.assertPositive(val, name);
    }

    @Deprecated(forRemoval = true)
    protected static int assertNonNegative(int val, String name) {
        return Validations.assertNonNegative(val, name);
    }

    @Deprecated(forRemoval = true)
    protected static int assertRange(int val, int min, int max, String name) {
        return Validations.assertRange(val, min, max, name);
    }

    @Deprecated(forRemoval = true)
    protected static float assertRange(float val, float min, float max, String name) {
        return Validations.assertRange(val, min, max, name);
    }

    @Deprecated(forRemoval = true)
    protected static double assertRange(double val, double min, double max, String name) {
        return Validations.assertRange(val, min, max, name);
    }

    @Deprecated(forRemoval = true)
    protected static float assertUnit(float val, String name) {
        return Validations.assertUnit(val, name);
    }

    @Deprecated(forRemoval = true)
    protected static double assertUnit(double val, String name) {
        return Validations.assertUnit(val, name);
    }

    @Deprecated(forRemoval = true)
    protected static IntProvider assertRange(IntProvider provider, int min, int max, String name) {
        return Validations.assertRange(provider, min, max, name);
    }

    @Deprecated(forRemoval = true)
    protected static IntProvider assertNonNegative(IntProvider provider, String name) {
        return Validations.assertNonNegative(provider, name);
    }

    @Deprecated(forRemoval = true)
    protected static IntProvider assertPositive(IntProvider provider, String name) {
        return Validations.assertPositive(provider, name);
    }

    @Deprecated(forRemoval = true)
    protected static FloatProvider assertRange(FloatProvider provider, float min, float max, String name) {
        return Validations.assertRange(provider, min, max, name);
    }

    @Contract("null, _ -> fail; _, _ -> !null")
    protected <T> T notNull(T t, String name) {
        return Validations.notNull(t, name, sourceLine);
    }

    protected <C extends Collection<? extends T>, T> C notEmpty(C collection, String name) {
        return Validations.notEmpty(collection, name, sourceLine);
    }

    protected <T> T validate(T t, Function<T, @Nullable String> errorMsgFunc) {
        return Validations.validate(t, errorMsgFunc, this::exception);
    }

    protected KubeRuntimeException exception(String message) {
        return Validations.exception(sourceLine, message)
                .customData("configured feature", id);
    }

    public transient PlacedFeatureBuilder placedFeature;

    public ConfiguredFeatureBuilder(ResourceLocation id) {
        super(id);
    }

    @Info("Create and modify the placed feature of the configured feature")
    public ConfiguredFeatureBuilder<FC> withPlacement(Context ctx, Consumer<PlacedFeatureBuilder> builder) {
        return placement(ctx, id, builder);
    }

    @Info(
            value = "Create and modify the placed feature of the configured feature",
            params = @Param(name = "id", value = "The id the placed feature will be create with")
    )
    public ConfiguredFeatureBuilder<FC> withPlacement(Context ctx, KubeResourceLocation id, Consumer<PlacedFeatureBuilder> builder) {
        return placement(ctx, id.wrapped(), builder);
    }

    protected ConfiguredFeatureBuilder<FC> placement(Context ctx, ResourceLocation id, Consumer<PlacedFeatureBuilder> builder) {
        placedFeature = new PlacedFeatureBuilder(id);
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

        public NoneConfig(ResourceLocation id, Supplier<? extends Feature<NoneFeatureConfiguration>> feature) {
            super(id, feature);
        }

        @Override
        protected NoneFeatureConfiguration createFeatureConfiguration() {
            return FeatureConfiguration.NONE;
        }
    }

    public abstract static class WithFeature<FC extends FeatureConfiguration> extends ConfiguredFeatureBuilder<FC> {

        private final Supplier<? extends Feature<FC>> feature;

        public WithFeature(ResourceLocation id, Supplier<? extends Feature<FC>> feature) {
            super(id);
            this.feature = feature;
        }

        @Override
        protected Feature<FC> getFeature() {
            return feature.get();
        }
    }
}
