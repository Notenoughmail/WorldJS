package io.github.notenoughmail.worldjs.util.event;

import dev.latvian.mods.rhino.type.TypeInfo;
import io.github.notenoughmail.worldjs.builders.base.SubBuilder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class SubBuilderEvent<C, B extends SubBuilder<?>> extends Event {

    private final BiConsumer<ResourceLocation, SubBuilder.SubBuilderType<C, ? extends B>> receptor;

    protected SubBuilderEvent(BiConsumer<ResourceLocation, SubBuilder.SubBuilderType<C, ? extends B>> receptor) {
        this.receptor = receptor;
    }

    public <P extends B> void register(
            ResourceLocation typeId,
            Class<P> type,
            Function<C, P> constructor
    ) {
        register(typeId, TypeInfo.of(type), constructor);
    }

    public void register(
            ResourceLocation typeId,
            TypeInfo type,
            Function<C, ? extends B> constructor
    ) {
        receptor.accept(typeId, new SubBuilder.SubBuilderType<>(type, constructor));
    }
}
