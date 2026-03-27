package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FossilFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.List;

@ReturnsSelf
public class FossilConfigurationBuilder extends ConfiguredFeatureBuilder<FossilFeatureConfiguration, Feature<FossilFeatureConfiguration>> {

    public transient List<ResourceLocation> fossilStructures, overlayStructures;
    public transient Holder.Reference<StructureProcessorList> fossilProcessors, overlayProcessors;
    public transient int maxEmptyCornersAllowed;

    public FossilConfigurationBuilder(ResourceLocation id) {
        super(id);
        fossilStructures = List.of();
        overlayStructures = List.of();
        maxEmptyCornersAllowed = 0;
    }

    public FossilConfigurationBuilder fossilStructures(List<ResourceLocation> structures) {
        fossilStructures = structures;
        return this;
    }

    public FossilConfigurationBuilder overlayStructures(List<ResourceLocation> structures) {
        overlayStructures = structures;
        return this;
    }

    public FossilConfigurationBuilder fossilProcessors(Holder.Reference<StructureProcessorList> processors) {
        fossilProcessors = processors;
        return this;
    }

    public FossilConfigurationBuilder overlayProcessors(Holder.Reference<StructureProcessorList> processors) {
        overlayProcessors = processors;
        return this;
    }

    public FossilConfigurationBuilder maxEmptyCorners(int corners) {
        maxEmptyCornersAllowed = corners;
        return this;
    }

    @Override
    protected FossilFeatureConfiguration createFeatureConfiguration() {
        return new FossilFeatureConfiguration(
                fossilStructures,
                overlayStructures,
                fossilProcessors,
                overlayProcessors,
                maxEmptyCornersAllowed
        );
    }

    @Override
    protected Feature<FossilFeatureConfiguration> getFeature() {
        return Feature.FOSSIL;
    }
}
