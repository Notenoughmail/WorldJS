package io.github.notenoughmail.worldjs.util.event;

import io.github.notenoughmail.worldjs.builders.base.BiomeSourceBuilder;
import io.github.notenoughmail.worldjs.builders.base.SubBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

public final class BiomeSourceTypeRegisterEvent extends SubBuilderEvent<
        ResourceLocation,
        BiomeSourceBuilder<?>
        > {

    public BiomeSourceTypeRegisterEvent(BiConsumer<ResourceLocation, SubBuilder.SubBuilderType<ResourceLocation, ? extends BiomeSourceBuilder<?>>> receptor) {
        super(receptor);
    }
}
