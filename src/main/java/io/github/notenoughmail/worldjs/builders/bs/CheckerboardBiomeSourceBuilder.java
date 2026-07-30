package io.github.notenoughmail.worldjs.builders.bs;

import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.BiomeSourceBuilder;
import io.github.notenoughmail.worldjs.util.ServerRegistryHolderSet;
import io.github.notenoughmail.worldjs.util.Validations;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.CheckerboardColumnBiomeSource;
import org.jetbrains.annotations.Nullable;

@ReturnsSelf
public class CheckerboardBiomeSourceBuilder extends BiomeSourceBuilder<CheckerboardColumnBiomeSource> {

    @Nullable
    public transient HolderSet<Biome> biomes;
    public transient int size = 2;

    public CheckerboardBiomeSourceBuilder(ResourceLocation id) {
        super(id);
    }

    public CheckerboardBiomeSourceBuilder size(int size) {
        this.size = Validations.assertRange(size, 0, 62, "size");
        return this;
    }

    public CheckerboardBiomeSourceBuilder biomes(ServerRegistryHolderSet<Biome> biomes) {
        this.biomes = biomes.convertWithValidation("biomes", sourceLine);
        return this;
    }

    @Override
    protected CheckerboardColumnBiomeSource create() {
        return new CheckerboardColumnBiomeSource(
                Validations.notNull(biomes, "biomes", sourceLine),
                size
        );
    }
}
