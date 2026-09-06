package io.github.notenoughmail.worldjs.builders.cg;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.WorldJS;
import io.github.notenoughmail.worldjs.builders.base.BiomeSourceBuilder;
import io.github.notenoughmail.worldjs.builders.base.ChunkGeneratorBuilder;
import io.github.notenoughmail.worldjs.util.Validations;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import java.util.function.Consumer;

@Info("Uses noise values to determine the shape and biomes of the world")
@ReturnsSelf
public class NoiseBasedChunkGeneratorBuilder extends ChunkGeneratorBuilder<NoiseBasedChunkGenerator> {

    public transient Holder.Reference<NoiseGeneratorSettings> noiseSettings;
    public transient BiomeSource biomeSource;

    public NoiseBasedChunkGeneratorBuilder(ResourceKey<LevelStem> id) {
        super(id);
    }

    @Info("The noise settings of the world")
    public NoiseBasedChunkGeneratorBuilder noiseSettings(Holder.Reference<NoiseGeneratorSettings> settings) {
        noiseSettings = settings;
        return this;
    }

    @Info(value = "Settings for the biome layout of the world", params = {
            @Param(name = "type", value = "The biome source builder type to use for the world"),
            @Param(name = "biomeSourceBuilder", value = "Builder for the biome source")
    })
    public NoiseBasedChunkGeneratorBuilder biomeSource(
            Context ctx,
            ResourceLocation type,
            Consumer<BiomeSourceBuilder<?>> biomeSourceBuilder
    ) {
        biomeSource = WorldJS.BIOME_SOURCE_TYPES.build(ctx, type, type, biomeSourceBuilder);
        return this;
    }

    @Override
    protected NoiseBasedChunkGenerator create() {
        return new NoiseBasedChunkGenerator(
                Validations.notNull(biomeSource, "biomeSource", sourceLine),
                Validations.notNull(noiseSettings, "noiseSettings", sourceLine)
        );
    }
}
