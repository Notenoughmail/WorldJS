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
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
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

    protected static int assertPositive(int val, String name) {
        if (val < 1)
            throw new IllegalArgumentException("'" + name + "' must be > 0");
        return val;
    }

    protected static int assertNonNegative(int val, String name) {
        if (val < 0)
            throw new IllegalArgumentException("'" + name + "' must be >= 0");
        return val;
    }

    protected static int assertRange(int val, int min, int max, String name) {
        if (val < min || val > max)
            throw new IllegalArgumentException("'" + name + "' must be in the range [" + min + ", " + max + "]");
        return val;
    }

    protected static float assertRange(float val, float min, float max, String name) {
        if (val < min || val > max)
            throw new IllegalArgumentException("'%s' must be in the range [%.2f, %.2f]".formatted(name, min, max));
        return val;
    }

    protected static double assertRange(double val, double min, double max, String name) {
        if (val < min || val > max)
            throw new IllegalArgumentException("'%s' must be in the range [%.2f, %.2f]".formatted(name, min, max));
        return val;
    }

    protected static float assertUnit(float val, String name) {
        return assertRange(val, 0f, 1f, name);
    }

    protected static double assertUnit(double val, String name) {
        return assertRange(val, 0D, 1D, name);
    }

    protected static IntProvider assertRange(IntProvider provider, int min, int max, String name) {
        if (provider.getMinValue() < min || provider.getMaxValue() > max)
            throw new IllegalArgumentException("'" + name + "' must be in the range [" + min + ", " + max + "]");
        return provider;
    }

    protected static IntProvider assertNonNegative(IntProvider provider, String name) {
        if (provider.getMinValue() < 0)
            throw new IllegalArgumentException("'" + name + "' must be >= 0");
        return provider;
    }

    protected static IntProvider assertPositive(IntProvider provider, String name) {
        if (provider.getMinValue() < 1)
            throw new IllegalArgumentException("'" + name + "' must be > 0");
        return provider;
    }

    protected static FloatProvider assertRange(FloatProvider provider, float min, float max, String name) {
        if (provider.getMinValue() < min || provider.getMaxValue() > max)
            throw new IllegalArgumentException("'%s' must be in the range [%.2f, %.2f]".formatted(name, min, max));
        return provider;
    }

    protected <T> T notNull(T t, String name) {
        if (t == null) {
            throw exception("'" + name + "' must be defined!");
        }
        return t;
    }

    protected <C extends Collection<? extends T>, T> C notEmpty(C collection, String name) {
        if (collection.isEmpty()) {
            throw exception("'" + name + "' must not be empty!");
        }
        return collection;
    }

    protected <T> T validate(T t, Function<T, @Nullable String> errorMsgFunc) {
        final String str = errorMsgFunc.apply(t);
        if (str == null) return t;
        throw exception(str);
    }

    protected KubeRuntimeException exception(String message) {
        return new KubeRuntimeException(message)
                .source(sourceLine)
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
