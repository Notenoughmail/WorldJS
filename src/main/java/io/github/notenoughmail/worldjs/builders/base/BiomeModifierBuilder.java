package io.github.notenoughmail.worldjs.builders.base;

import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.util.ServerRegistryHolderSet;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.world.BiomeModifier;

@ReturnsSelf
public abstract class BiomeModifierBuilder<T extends BiomeModifier> extends BuilderBase<T> {

    protected KubeRuntimeException err(String msg) {
        return new KubeRuntimeException(msg)
                .source(sourceLine)
                .customData("biome modifier", id);
    }

    public transient HolderSet<Biome> biomes;

    public BiomeModifierBuilder(ResourceLocation id) {
        super(id);
        biomes = HolderSet.empty();
    }

    @Info("The biomes to modify")
    public BiomeModifierBuilder<T> biomes(ServerRegistryHolderSet<Biome> biomes) {
        this.biomes = biomes.verify(() -> {
            throw err("'biomes' should not be empty");
        });
        return this;
    }
}
