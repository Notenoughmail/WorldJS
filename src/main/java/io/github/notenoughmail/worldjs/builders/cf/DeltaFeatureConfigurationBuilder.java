package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.DeltaFeatureConfiguration;

@ReturnsSelf
public class DeltaFeatureConfigurationBuilder extends ConfiguredFeatureBuilder<DeltaFeatureConfiguration> {

    public transient BlockState contents, rim;
    public transient IntProvider size, rimSize;

    public DeltaFeatureConfigurationBuilder(ResourceLocation id) {
        super(id);
        contents = rim = Blocks.AIR.defaultBlockState();
    }

    @Info("The block to place on the inside of the delta")
    public DeltaFeatureConfigurationBuilder contents(BlockState state) {
        contents = state;
        return this;
    }

    @Info("The block to place on the rim of the delta")
    public DeltaFeatureConfigurationBuilder rim(BlockState state) {
        rim = state;
        return this;
    }

    @Info("The size of the inside of the delta, in the range [0, 16]")
    public DeltaFeatureConfigurationBuilder size(IntProvider provider) {
        size = assertRange(provider, 0, 16, "size");
        return this;
    }

    @Info("The size of the rim of the delta, in the range [0, 16]")
    public DeltaFeatureConfigurationBuilder rimSize(IntProvider provider) {
        rimSize = assertRange(provider, 0, 16, "rimSize");
        return this;
    }

    @Override
    protected DeltaFeatureConfiguration createFeatureConfiguration() {
        return new DeltaFeatureConfiguration(
                contents,
                rim,
                notNull(size, "size"),
                notNull(rimSize, "rimSize")
        );
    }

    @Override
    protected Feature<DeltaFeatureConfiguration> getFeature() {
        return Feature.DELTA_FEATURE;
    }
}
