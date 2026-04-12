package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

import java.util.function.Supplier;

@ReturnsSelf
public class BlockStateConfigurationBuilder extends ConfiguredFeatureBuilder.WithFeature<BlockStateConfiguration> {

    public transient BlockState state;

    public BlockStateConfigurationBuilder(ResourceLocation id, Supplier<Feature<BlockStateConfiguration>> feature) {
        super(id, feature);
        state = Blocks.AIR.defaultBlockState();
    }

    @Info("The state to place")
    public BlockStateConfigurationBuilder state(BlockState state) {
        this.state = state;
        return this;
    }

    @Override
    protected BlockStateConfiguration createFeatureConfiguration() {
        return new BlockStateConfiguration(state);
    }
}
