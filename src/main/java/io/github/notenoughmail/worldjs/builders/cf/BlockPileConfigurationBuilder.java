package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockPileConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

@ReturnsSelf
public class BlockPileConfigurationBuilder extends ConfiguredFeatureBuilder<BlockPileConfiguration> {

    public transient BlockStateProvider provider;

    public BlockPileConfigurationBuilder(ResourceLocation id) {
        super(id);
        provider = BlockStateProvider.simple(Blocks.AIR);
    }

    @Info("The block to place")
    public BlockPileConfigurationBuilder stateProvider(BlockStateProvider provider) {
        this.provider = provider;
        return this;
    }

    @Override
    protected BlockPileConfiguration createFeatureConfiguration() {
        return new BlockPileConfiguration(
                notNull(provider, "stateProvider")
        );
    }

    @Override
    protected Feature<BlockPileConfiguration> getFeature() {
        return Feature.BLOCK_PILE;
    }
}
