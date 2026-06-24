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

import java.util.EnumSet;
import java.util.Set;

@ReturnsSelf
public class RemoveCarversBiomeModifierBuilder extends BiomeModifierBuilder<BiomeModifiers.RemoveCarversBiomeModifier> {

    public transient HolderSet<ConfiguredWorldCarver<?>> carvers;
    public transient Set<GenerationStep.Carving> steps;

    public RemoveCarversBiomeModifierBuilder(ResourceLocation id) {
        super(id);
        carvers = HolderSet.empty();
        steps = EnumSet.allOf(GenerationStep.Carving.class);
    }

    @Info("The carvers to remove from the biomes(s)")
    public RemoveCarversBiomeModifierBuilder carvers(ServerRegistryHolderSet<ConfiguredWorldCarver<?>> carvers) {
        this.carvers = carvers.verify(() -> {
            throw err("'carvers' should not be empty");
        });
        return this;
    }

    @Info("The carving steps to remove the carver(s) from")
    public RemoveCarversBiomeModifierBuilder steps(Set<GenerationStep.Carving> steps) {
        this.steps = steps;
        return this;
    }

    @Override
    public BiomeModifiers.RemoveCarversBiomeModifier createObject() {
        return new BiomeModifiers.RemoveCarversBiomeModifier(
                biomes,
                carvers,
                steps
        );
    }
}
