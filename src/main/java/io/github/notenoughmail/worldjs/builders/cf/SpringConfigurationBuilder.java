package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.typings.Info;
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
public class SpringConfigurationBuilder extends ConfiguredFeatureBuilder<SpringConfiguration> {

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

    @Info("The fluid to place, derived from the given block")
    public SpringConfigurationBuilder fluidState(BlockState state) {
        return rawFluidState(state.getFluidState());
    }

    @Info("The fluid to place")
    public SpringConfigurationBuilder rawFluidState(FluidState fluidState) {
        this.fluidState = fluidState;
        return this;
    }

    @Info("If the spring requires a block matching the spring's valid blocks below it")
    public SpringConfigurationBuilder requiresRocksBelow(boolean required) {
        requiresRockBelow = required;
        return this;
    }

    @Info("The number of blocks in valid blocks that must be adjacent to the spring for it to generate")
    public SpringConfigurationBuilder rockCount(int count) {
        rockCount = count;
        return this;
    }

    @Info("The number of air blocks that must be adjacent to the spring for ti to generate")
    public SpringConfigurationBuilder holeCount(int count) {
        holeCount = count;
        return this;
    }

    @Info("The blocks the spring requires to generate")
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
