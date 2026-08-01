package io.github.notenoughmail.worldjs.builders.base;

import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.WorldJS;
import io.github.notenoughmail.worldjs.util.event.BiomeSourceTypeRegisterEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.BiomeSource;

import java.util.Map;
import java.util.function.Supplier;

@ReturnsSelf
public abstract class BiomeSourceBuilder<B extends BiomeSource> extends SubBuilder<B> {

    public static final Supplier<Map<
            ResourceLocation,
            SubBuilderInfo<ResourceLocation, ? extends BiomeSourceBuilder<?>>
            >> ALL_TYPES = WorldJS.eventMap(BiomeSourceTypeRegisterEvent::new);

    protected final ResourceLocation typeId;

    public BiomeSourceBuilder(ResourceLocation id) {
        typeId = id;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + typeId + "]" + "@" + Integer.toHexString(hashCode());
    }
}
