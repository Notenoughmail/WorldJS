package io.github.notenoughmail.worldjs.builders.bs;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.BiomeSourceBuilder;
import io.github.notenoughmail.worldjs.util.Validations;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.FixedBiomeSource;
import org.jetbrains.annotations.Nullable;

@Info("A single biome across the whole world")
@ReturnsSelf
public class FixedBiomeSourceBuilder extends BiomeSourceBuilder<FixedBiomeSource> {

    @Nullable
    public transient Holder.Reference<Biome> biome;

    public FixedBiomeSourceBuilder(ResourceLocation id) {
        super(id);
    }

    @Info("The single biome to use")
    public FixedBiomeSourceBuilder biome(Holder.Reference<Biome> biome) {
        this.biome = biome;
        return this;
    }

    @Override
    protected FixedBiomeSource create() {
        return new FixedBiomeSource(Validations.notNull(biome, "biome", sourceLine));
    }
}
