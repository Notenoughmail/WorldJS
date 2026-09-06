package io.github.notenoughmail.worldjs.builders.base;

import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;

@ReturnsSelf
public abstract class ChunkGeneratorBuilder<G extends ChunkGenerator> extends SubBuilder<G> {

    protected final ResourceKey<LevelStem> id;

    public ChunkGeneratorBuilder(ResourceKey<LevelStem> id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + id.location() + "}@" + Integer.toHexString(hashCode());
    }
}
