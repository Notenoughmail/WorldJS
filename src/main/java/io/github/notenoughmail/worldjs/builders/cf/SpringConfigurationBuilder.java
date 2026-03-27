package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;
import net.minecraft.world.level.material.FluidState;

@ReturnsSelf
public class SpringConfigurationBuilder extends ConfiguredFeatureBuilder<SpringConfiguration, Feature<SpringConfiguration>> {

    public transient FluidState fluidState;
    public transient boolean requiresRockBelow;
    public transient int rockCount, holeCount;
    public transient HolderSet<Block> validBlocks;

    public SpringConfigurationBuilder(ResourceLocation id) {
        super(id);
        fluidState = Blocks.AIR.defaultBlockState().getFluidState();
        requiresRockBelow = true;
        rockCount = 4;
        holeCount = 1;
        validBlocks = HolderSet.empty();
    }

    public SpringConfigurationBuilder fluidState(BlockState state) {
        return rawFluidState(state.getFluidState());
    }

    public SpringConfigurationBuilder rawFluidState(FluidState fluidState) {
        this.fluidState = fluidState;
        return this;
    }

    public SpringConfigurationBuilder requiresRocksBelow(boolean required) {
        requiresRockBelow = required;
        return this;
    }

    public SpringConfigurationBuilder rockCount(int count) {
        rockCount = count;
        return this;
    }

    public SpringConfigurationBuilder holeCount(int count) {
        holeCount = count;
        return this;
    }

    public SpringConfigurationBuilder validBlocks(HolderSet<Block> blocks) {
        validBlocks = blocks;
        return this;
    }

    @Override
    protected SpringConfiguration createFeatureConfiguration() {
        return new SpringConfiguration(
                fluidState,
                requiresRockBelow,
                rockCount,
                holeCount,
                validBlocks
        );
    }

    @Override
    protected Feature<SpringConfiguration> getFeature() {
        return Feature.SPRING;
    }
}
