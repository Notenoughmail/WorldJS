package io.github.notenoughmail.worldjs.builders.base;

import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.WorldJS;
import io.github.notenoughmail.worldjs.util.event.ChunkGeneratorTypeRegisterEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.Map;
import java.util.function.Supplier;

@ReturnsSelf
public abstract class ChunkGeneratorBuilder<G extends ChunkGenerator> extends SubBuilder<G> {

    public static final Supplier<Map<
            ResourceLocation,
            Info<ResourceKey<LevelStem>, ? extends ChunkGeneratorBuilder<?>>
            >> ALL_TYPES = WorldJS.eventMap(ChunkGeneratorTypeRegisterEvent::new);

    protected final ResourceKey<LevelStem> id;

    public ChunkGeneratorBuilder(ResourceKey<LevelStem> id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + id.location() + "}@" + Integer.toHexString(hashCode());
    }
}
