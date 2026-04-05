package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockColumnConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.ArrayList;
import java.util.List;

@ReturnsSelf
public class BlockColumnConfigurationBuilder extends ConfiguredFeatureBuilder<BlockColumnConfiguration> {

    public transient final List<BlockColumnConfiguration.Layer> layers;
    public transient Direction direction;
    public transient BlockPredicate allowedPlacement;
    public transient boolean prioritizeTip;

    public BlockColumnConfigurationBuilder(ResourceLocation id) {
        super(id);
        layers = new ArrayList<>();
        direction = Direction.UP;
        allowedPlacement = BlockPredicate.alwaysTrue();
        prioritizeTip = false;
    }

    public BlockColumnConfigurationBuilder layer(IntProvider height, BlockStateProvider state) {
        layers.add(BlockColumnConfiguration.layer(
                assertRange(height, 1, Integer.MAX_VALUE, "height"),
                state
        ));
        return this;
    }

    public BlockColumnConfigurationBuilder direction(Direction dir) {
        direction = dir;
        return this;
    }

    public BlockColumnConfigurationBuilder allowedPlacement(BlockPredicate allowed) {
        allowedPlacement = allowed;
        return this;
    }

    public BlockColumnConfigurationBuilder prioritizeTip() {
        prioritizeTip = true;
        return this;
    }

    @Override
    protected BlockColumnConfiguration createFeatureConfiguration() {
        return new BlockColumnConfiguration(
                layers,
                direction,
                allowedPlacement,
                prioritizeTip
        );
    }

    @Override
    protected Feature<BlockColumnConfiguration> getFeature() {
        return Feature.BLOCK_COLUMN;
    }
}
