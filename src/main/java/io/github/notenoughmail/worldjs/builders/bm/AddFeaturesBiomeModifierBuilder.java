package io.github.notenoughmail.worldjs.builders.bm;

import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.BiomeModifierBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifiers;

@ReturnsSelf
public class AddFeaturesBiomeModifierBuilder extends BiomeModifierBuilder<BiomeModifiers.AddFeaturesBiomeModifier> {

    public transient HolderSet<PlacedFeature> features;
    public transient GenerationStep.Decoration step;

    public AddFeaturesBiomeModifierBuilder(ResourceLocation id) {
        super(id);
        features = HolderSet.empty();
        step = GenerationStep.Decoration.UNDERGROUND_DECORATION;
    }

    public AddFeaturesBiomeModifierBuilder step(GenerationStep.Decoration step) {
        this.step = step;
        return this;
    }

    public AddFeaturesBiomeModifierBuilder features(HolderSet<PlacedFeature> features) {
        this.features = features;
        return this;
    }

    @Override
    public BiomeModifiers.AddFeaturesBiomeModifier createObject() {
        return new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes,
                features,
                step
        );
    }
}
