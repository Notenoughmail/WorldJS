package io.github.notenoughmail.worldjs.builders.cg;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ChunkGeneratorBuilder;
import io.github.notenoughmail.worldjs.util.Validations;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import org.jetbrains.annotations.Nullable;

@Info("Contains all possible blocks states laid out in a grid at y = 70")
@ReturnsSelf
public class DebugChunkGeneratorBuilder extends ChunkGeneratorBuilder<DebugLevelSource> {

    @Nullable
    public transient Holder.Reference<Biome> biome;

    public DebugChunkGeneratorBuilder(ResourceKey<LevelStem> id) {
        super(id);
    }

    @Info("The biome to place the states in")
    public DebugChunkGeneratorBuilder biome(Holder.Reference<Biome> biome) {
        this.biome = biome;
        return this;
    }

    @Override
    protected DebugLevelSource create() {
        return new DebugLevelSource(Validations.notNull(biome, "biome", sourceLine));
    }
}
