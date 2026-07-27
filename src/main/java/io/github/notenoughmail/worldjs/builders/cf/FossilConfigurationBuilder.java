package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import io.github.notenoughmail.worldjs.util.Validations;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FossilFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.List;

@ReturnsSelf
public class FossilConfigurationBuilder extends ConfiguredFeatureBuilder<FossilFeatureConfiguration> {

    public transient List<ResourceLocation> fossilStructures, overlayStructures;
    public transient Holder.Reference<StructureProcessorList> fossilProcessors, overlayProcessors;
    public transient int maxEmptyCornersAllowed;

    public FossilConfigurationBuilder(ResourceLocation id) {
        super(id);
        fossilStructures = List.of();
        overlayStructures = List.of();
        maxEmptyCornersAllowed = 0;
    }

    @Info("The ids of fossil structure templates to choose for placing. Must be the same amount as overlay structures")
    public FossilConfigurationBuilder fossilStructures(List<ResourceLocation> structures) {
        fossilStructures = structures;
        return this;
    }

    @Info("The ids of overlay structure templates to choose for placing. Must be the same amount as fossil structures")
    public FossilConfigurationBuilder overlayStructures(List<ResourceLocation> structures) {
        overlayStructures = structures;
        return this;
    }

    @Info("The fossil structure template processor to use")
    public FossilConfigurationBuilder fossilProcessors(Holder.Reference<StructureProcessorList> processors) {
        fossilProcessors = processors;
        return this;
    }

    @Info("The overlay structure template processor to use")
    public FossilConfigurationBuilder overlayProcessors(Holder.Reference<StructureProcessorList> processors) {
        overlayProcessors = processors;
        return this;
    }

    @Info("How many corners may be empty while allowing the feature to generate, in the range [0, 7]")
    public FossilConfigurationBuilder maxEmptyCorners(int corners) {
        maxEmptyCornersAllowed = Validations.assertRange(corners, 0, 7, "maxEmptyCorners");
        return this;
    }

    @Override
    protected FossilFeatureConfiguration createFeatureConfiguration() {
        if (fossilStructures.size() != overlayStructures.size())
            throw new KubeRuntimeException("Must have same number of fossil and overlay structures!")
                    .source(sourceLine);
        return new FossilFeatureConfiguration(
                fossilStructures,
                overlayStructures,
                notNull(fossilProcessors, "fossilProcessors"),
                notNull(overlayProcessors, "overlayProcessors"),
                maxEmptyCornersAllowed
        );
    }

    @Override
    protected Feature<FossilFeatureConfiguration> getFeature() {
        return Feature.FOSSIL;
    }
}
