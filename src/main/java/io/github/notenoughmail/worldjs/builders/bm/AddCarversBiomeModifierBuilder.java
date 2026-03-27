package io.github.notenoughmail.worldjs.builders.bm;

import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.BiomeModifierBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.neoforged.neoforge.common.world.BiomeModifiers;

@ReturnsSelf
public class AddCarversBiomeModifierBuilder extends BiomeModifierBuilder<BiomeModifiers.AddCarversBiomeModifier> {

    public transient HolderSet<ConfiguredWorldCarver<?>> carvers;
    public transient GenerationStep.Carving step;

    public AddCarversBiomeModifierBuilder(ResourceLocation id) {
        super(id);
        carvers = HolderSet.empty();
        step = GenerationStep.Carving.AIR;
    }

    public AddCarversBiomeModifierBuilder carvers(HolderSet<ConfiguredWorldCarver<?>> carvers) {
        this.carvers = carvers;
        return this;
    }

    public AddCarversBiomeModifierBuilder step(GenerationStep.Carving step) {
        this.step = step;
        return this;
    }

    @Override
    public BiomeModifiers.AddCarversBiomeModifier createObject() {
        return new BiomeModifiers.AddCarversBiomeModifier(
                biomes,
                carvers,
                step
        );
    }
}
