package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

@ReturnsSelf
public class LakeConfigurationBuilder extends ConfiguredFeatureBuilder<LakeFeature.Configuration> {

    public transient BlockStateProvider fluid, barrier;

    public LakeConfigurationBuilder(ResourceLocation id) {
        super(id);
    }

    public LakeConfigurationBuilder fluid(BlockStateProvider provider) {
        fluid = provider;
        return this;
    }

    public LakeConfigurationBuilder barrier(BlockStateProvider provider) {
        barrier = provider;
        return this;
    }

    @Override
    protected LakeFeature.Configuration createFeatureConfiguration() {
        return new LakeFeature.Configuration(
                notNull(fluid, "Fluid must be defined!"),
                notNull(barrier, "Barrier must be defined!")
        );
    }

    @Override
    protected Feature<LakeFeature.Configuration> getFeature() {
        return Feature.LAKE;
    }
}
