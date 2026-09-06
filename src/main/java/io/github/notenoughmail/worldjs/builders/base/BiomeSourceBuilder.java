package io.github.notenoughmail.worldjs.builders.base;

import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.BiomeSource;

@ReturnsSelf
public abstract class BiomeSourceBuilder<B extends BiomeSource> extends SubBuilder<B> {

    protected final ResourceLocation typeId;

    public BiomeSourceBuilder(ResourceLocation id) {
        typeId = id;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + typeId + "]" + "@" + Integer.toHexString(hashCode());
    }
}
