package io.github.notenoughmail.worldjs.builders.bm;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.BiomeModifierBuilder;
import io.github.notenoughmail.worldjs.util.ServerRegistryHolderSet;
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

    @Info("The carvers to add to the biome(s)")
    public AddCarversBiomeModifierBuilder carvers(ServerRegistryHolderSet<ConfiguredWorldCarver<?>> carvers) {
        this.carvers = carvers.convertWithValidation("carvers", this::err);
        return this;
    }

    @Info("The carving step to add the carver(s) to")
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
