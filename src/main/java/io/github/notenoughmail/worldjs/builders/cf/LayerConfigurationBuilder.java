package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.LayerConfiguration;

@ReturnsSelf
public class LayerConfigurationBuilder extends ConfiguredFeatureBuilder<LayerConfiguration> {

    public transient int height;
    public transient BlockState state;

    public LayerConfigurationBuilder(ResourceLocation id) {
        super(id);
        state = Blocks.AIR.defaultBlockState();
    }

    @Info("The layer to fill, in the range [0, 4064]")
    public LayerConfigurationBuilder height(int height) {
        this.height = assertRange(height, 0, DimensionType.Y_SIZE, "height");
        return this;
    }

    @Info("The block to fill the layer with")
    public LayerConfigurationBuilder state(BlockState state) {
        this.state = state;
        return this;
    }

    @Override
    protected LayerConfiguration createFeatureConfiguration() {
        return new LayerConfiguration(
                height,
                state
        );
    }

    @Override
    protected Feature<LayerConfiguration> getFeature() {
        return Feature.FILL_LAYER;
    }
}
