package io.github.notenoughmail.worldjs.builders.cg;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.BiomeSourceBuilder;
import io.github.notenoughmail.worldjs.builders.base.ChunkGeneratorBuilder;
import io.github.notenoughmail.worldjs.builders.base.SubBuilder;
import io.github.notenoughmail.worldjs.util.Validations;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import java.util.function.Consumer;

@ReturnsSelf
public class NoiseBasedChunkGeneratorBuilder extends ChunkGeneratorBuilder<NoiseBasedChunkGenerator> {

    public transient Holder.Reference<NoiseGeneratorSettings> noiseSettings;
    public transient BiomeSource biomeSource;

    public NoiseBasedChunkGeneratorBuilder(ResourceKey<LevelStem> id) {
        super(id);
    }

    public NoiseBasedChunkGeneratorBuilder noiseSettings(Holder.Reference<NoiseGeneratorSettings> settings) {
        noiseSettings = settings;
        return this;
    }

    public NoiseBasedChunkGeneratorBuilder biomeSource(Context ctx, ResourceLocation type, Consumer<BiomeSourceBuilder<?>> biomeSourceBuilder) {
        biomeSource = SubBuilder.build(
                ctx,
                type,
                type,
                biomeSourceBuilder,
                BiomeSourceBuilder.ALL_TYPES,
                "biome source"
        );
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
