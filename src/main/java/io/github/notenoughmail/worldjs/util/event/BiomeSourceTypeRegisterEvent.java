package io.github.notenoughmail.worldjs.util.event;

import dev.latvian.mods.rhino.type.TypeInfo;
import io.github.notenoughmail.worldjs.builders.base.BiomeSourceBuilder;
import io.github.notenoughmail.worldjs.builders.base.SubBuilder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;

import java.util.function.BiConsumer;
import java.util.function.Function;

public final class BiomeSourceTypeRegisterEvent extends Event {

    private final BiConsumer<ResourceLocation, SubBuilder.Info<ResourceLocation, ? extends BiomeSourceBuilder<?>>> receptor;

    public BiomeSourceTypeRegisterEvent(BiConsumer<ResourceLocation, SubBuilder.Info<ResourceLocation, ? extends BiomeSourceBuilder<?>>> receptor) {
        this.receptor = receptor;
    }

    public <T extends BiomeSourceBuilder<?>> void register(
            ResourceLocation typeId,
            Class<T> type,
            Function<ResourceLocation, T> constructor
    ) {
        register(typeId, TypeInfo.of(type), constructor);
    }

    public <T extends BiomeSourceBuilder<?>> void register(
            ResourceLocation typeId,
            TypeInfo type,
            Function<ResourceLocation, T> constructor
    ) {
        receptor.accept(typeId, new SubBuilder.Info<>(type, constructor));
    }
}
