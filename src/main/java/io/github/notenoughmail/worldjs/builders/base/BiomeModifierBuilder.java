package io.github.notenoughmail.worldjs.builders.base;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.world.BiomeModifier;

@ReturnsSelf
public abstract class BiomeModifierBuilder<T extends BiomeModifier> extends BuilderBase<T> {

    public transient HolderSet<Biome> biomes;

    public BiomeModifierBuilder(ResourceLocation id) {
        super(id);
        biomes = HolderSet.empty();
    }

    @Info("The biomes to modify")
    public BiomeModifierBuilder<T> biomes(HolderSet<Biome> biomes) {
        this.biomes = biomes;
        return this;
    }
}
