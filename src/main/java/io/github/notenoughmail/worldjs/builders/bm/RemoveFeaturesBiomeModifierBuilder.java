package io.github.notenoughmail.worldjs.builders.bm;

import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.BiomeModifierBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifiers;

import java.util.EnumSet;
import java.util.Set;

@ReturnsSelf
public class RemoveFeaturesBiomeModifierBuilder extends BiomeModifierBuilder<BiomeModifiers.RemoveFeaturesBiomeModifier> {

    public transient HolderSet<PlacedFeature> features;
    public transient Set<GenerationStep.Decoration> steps;

    public RemoveFeaturesBiomeModifierBuilder(ResourceLocation id) {
        super(id);
        features = HolderSet.empty();
        steps = EnumSet.allOf(GenerationStep.Decoration.class);
    }

    public RemoveFeaturesBiomeModifierBuilder features(HolderSet<PlacedFeature> features) {
        this.features = features;
        return this;
    }

    public RemoveFeaturesBiomeModifierBuilder steps(Set<GenerationStep.Decoration> steps) {
        this.steps = steps;
        return this;
    }

    @Override
    public BiomeModifiers.RemoveFeaturesBiomeModifier createObject() {
        return new BiomeModifiers.RemoveFeaturesBiomeModifier(
                biomes,
                features,
                steps
        );
    }
}
