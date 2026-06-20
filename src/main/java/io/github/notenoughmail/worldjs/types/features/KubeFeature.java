package io.github.notenoughmail.worldjs.types.features;

import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.BuilderType;
import dev.latvian.mods.kubejs.registry.BuilderTypeRegistryHandler;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.rhino.util.HideFromJS;
import io.github.notenoughmail.worldjs.WorldJS;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import io.github.notenoughmail.worldjs.util.mixin.InfoAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class KubeFeature extends Feature<NoneFeatureConfiguration> {

    private final KubeFeaturePlaceFunction placeFunction;

    public KubeFeature(KubeFeaturePlaceFunction placeFunction) {
        super(NoneFeatureConfiguration.CODEC);
        this.placeFunction = placeFunction;
    }

    @Deprecated
    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        return false;
    }

    @Override
    public boolean place(NoneFeatureConfiguration config, WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
        return level.ensureCanWrite(origin) && placeFunction.placeSafe(level, chunkGenerator, random, origin);
    }

    @FunctionalInterface
    public interface KubeFeaturePlaceFunction {

        boolean place(
                WorldGenLevel level,
                ChunkGenerator chunkGenerator,
                RandomSource random,
                BlockPos origin
        );

        @HideFromJS
        default boolean placeSafe(
                WorldGenLevel level,
                ChunkGenerator chunkGenerator,
                RandomSource random,
                BlockPos origin
        ) {
            try {
                return place(level, chunkGenerator, random, origin);
            } catch (Exception e) {
                WorldJS.scriptErrorOnce("Error encountered while placing feature", e);
                return false;
            }
        }
    }

    public static class Builder extends BuilderBase<KubeFeature> {

        public transient KubeFeaturePlaceFunction placeFunction;

        public Builder(ResourceLocation id) {
            super(id);
            final BuilderTypeRegistryHandler.Info<ConfiguredFeature<?, ?>> info = Cast.to(BuilderTypeRegistryHandler.INFO.get()
                    .computeIfAbsent(Registries.CONFIGURED_FEATURE, k -> { throw new KubeRuntimeException("No configured feature info?"); }));
            final BuilderType<ConfiguredFeature<?, ?>> prev = info.namedType(id);
            if (prev != null) {
                throw new KubeRuntimeException("Previous '" + id + "' type '" + prev.builderClass().getName() + "' for registry '" + info + "' already exists!")
                        .customData("id", id)
                        .customData("registry", info)
                        .customData("previous", prev);
            }
            final BuilderType<ConfiguredFeature<?, ?>> builderType = new BuilderType<>(
                    id,
                    ConfiguredFeatureBuilder.NoneConfig.class,
                    ConfiguredFeatureBuilder.supplierFactory(
                            this::get,
                            ConfiguredFeatureBuilder.NoneConfig::new
                    )
            );
            Cast.<InfoAccessor<ConfiguredFeature<?, ?>>>to(info).worldjs$GetTypes().put(id, builderType);
        }

        public Builder placeFunction(KubeFeaturePlaceFunction function) {
            placeFunction = function;
            return this;
        }

        @Override
        public KubeFeature createObject() {
            if (placeFunction == null) {
                throw new KubeRuntimeException("'placeFunction' for '" + id + "' must be defined!")
                        .source(sourceLine);
            }
            return new KubeFeature(placeFunction);
        }
    }
}
