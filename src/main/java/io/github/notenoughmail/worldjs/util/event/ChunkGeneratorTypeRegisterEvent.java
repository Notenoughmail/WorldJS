package io.github.notenoughmail.worldjs.util.event;

import io.github.notenoughmail.worldjs.builders.base.ChunkGeneratorBuilder;
import io.github.notenoughmail.worldjs.builders.base.SubBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.function.BiConsumer;

public final class ChunkGeneratorTypeRegisterEvent extends SubBuilderEvent<
        ResourceKey<LevelStem>,
        ChunkGeneratorBuilder<?>
        > {

    public ChunkGeneratorTypeRegisterEvent(BiConsumer<ResourceLocation, SubBuilder.SubBuilderType<ResourceKey<LevelStem>, ? extends ChunkGeneratorBuilder<?>>> receptor) {
        super(receptor);
    }
}
