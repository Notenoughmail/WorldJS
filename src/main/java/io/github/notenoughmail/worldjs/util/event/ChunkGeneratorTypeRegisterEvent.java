package io.github.notenoughmail.worldjs.util.event;

import dev.latvian.mods.rhino.type.TypeInfo;
import io.github.notenoughmail.worldjs.builders.base.ChunkGeneratorBuilder;
import io.github.notenoughmail.worldjs.builders.base.SubBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.LevelStem;
import net.neoforged.bus.api.Event;

import java.util.function.BiConsumer;
import java.util.function.Function;

public final class ChunkGeneratorTypeRegisterEvent extends Event {

    private final BiConsumer<ResourceLocation, SubBuilder.SubBuilderInfo<ResourceKey<LevelStem>, ? extends ChunkGeneratorBuilder<?>>> receptor;

    public ChunkGeneratorTypeRegisterEvent(BiConsumer<ResourceLocation, SubBuilder.SubBuilderInfo<ResourceKey<LevelStem>, ? extends ChunkGeneratorBuilder<?>>> receptor) {
        this.receptor = receptor;
    }

    public <T extends ChunkGeneratorBuilder<?>> void register(
            ResourceLocation typeId,
            Class<T> type,
            Function<ResourceKey<LevelStem>, T> constructor
    ) {
        register(typeId, TypeInfo.of(type), constructor);
    }

    public <T extends ChunkGeneratorBuilder<?>> void register(
            ResourceLocation typeId,
            TypeInfo type,
            Function<ResourceKey<LevelStem>, T> constructor
    ) {
        receptor.accept(typeId, new SubBuilder.SubBuilderInfo<>(type, constructor));
    }
}
