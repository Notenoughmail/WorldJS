package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NetherForestVegetationConfig;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

@ReturnsSelf
public class NetherForestVegetationConfigBuilder extends ConfiguredFeatureBuilder<NetherForestVegetationConfig> {

    public transient BlockStateProvider provider;
    public transient int width, height;

    public NetherForestVegetationConfigBuilder(ResourceLocation id) {
        super(id);
        provider = BlockStateProvider.simple(Blocks.AIR);
        width = height = 1;
    }

    @Info("The block to place")
    public NetherForestVegetationConfigBuilder stateProvider(BlockStateProvider provider) {
        this.provider = provider;
        return this;
    }

    @Info("The horizontal distance to spread over, must be positive")
    public NetherForestVegetationConfigBuilder spreadWidth(int width) {
        this.width = assertPositive(width, "Spread width must be positive");
        return this;
    }

    @Info("the vertical distance to spread over, must be positive")
    public NetherForestVegetationConfigBuilder spreadHeight(int height) {
        this.height = assertPositive(height, "Spread height must be positive");
        return this;
    }

    @Override
    protected NetherForestVegetationConfig createFeatureConfiguration() {
        return new NetherForestVegetationConfig(
                provider,
                width,
                height
        );
    }

    @Override
    protected Feature<NetherForestVegetationConfig> getFeature() {
        return Feature.NETHER_FOREST_VEGETATION;
    }
}
