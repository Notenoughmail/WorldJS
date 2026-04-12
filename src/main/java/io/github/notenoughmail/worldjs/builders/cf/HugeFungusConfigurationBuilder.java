package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.HugeFungusConfiguration;

@ReturnsSelf
public class HugeFungusConfigurationBuilder extends ConfiguredFeatureBuilder<HugeFungusConfiguration> {

    public transient BlockState validBaseState, stemState, hatState, decorState;
    public transient BlockPredicate replaceableBlocks;
    public transient boolean planted;

    public HugeFungusConfigurationBuilder(ResourceLocation id) {
        super(id);
    }

    @Info("The block to place the feature on")
    public HugeFungusConfigurationBuilder validBaseBlock(BlockState state) {
        validBaseState = state;
        return this;
    }

    @Info("The block to place for the stem")
    public HugeFungusConfigurationBuilder stemState(BlockState state) {
        state = stemState;
        return this;
    }

    @Info("The bloc kto place for the hat")
    public HugeFungusConfigurationBuilder hatState(BlockState state) {
        hatState = state;
        return this;
    }

    @Info("The bloc kto use as decoration")
    public HugeFungusConfigurationBuilder decorState(BlockState state) {
        decorState = state;
        return this;
    }

    @Info("The blocks that may be replaced by the feature")
    public HugeFungusConfigurationBuilder replaceableBlocks(BlockPredicate replaceable) {
        replaceableBlocks = replaceable;
        return this;
    }

    @Info("Allows the feature to exceed the world ceiling and makes blocks replaced by the feature drop their items")
    public HugeFungusConfigurationBuilder planted() {
        planted = true;
        return this;
    }

    @Override
    protected HugeFungusConfiguration createFeatureConfiguration() {
        return new HugeFungusConfiguration(
                notNull(validBaseState, "validBaseBlock"),
                notNull(stemState, "stemState"),
                notNull(hatState, "hatState"),
                notNull(decorState, "decorState"),
                notNull(replaceableBlocks, "replaceableBlocks"),
                planted
        );
    }

    @Override
    protected Feature<HugeFungusConfiguration> getFeature() {
        return Feature.HUGE_FUNGUS;
    }
}
