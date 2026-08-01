package io.github.notenoughmail.worldjs.builders.bs;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import io.github.notenoughmail.worldjs.builders.base.BiomeSourceBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.TheEndBiomeSource;

@Info("The biome source used for the End")
public class EndBiomeSourceBuilder extends BiomeSourceBuilder<TheEndBiomeSource> {

    public EndBiomeSourceBuilder(ResourceLocation id) {
        super(id);
    }

    @Override
    protected TheEndBiomeSource create() {
        return TheEndBiomeSource.create(RegistryAccessContainer.current.access().lookupOrThrow(Registries.BIOME));
    }
}
