package io.github.notenoughmail.worldjs.builders.cf;

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

    public HugeFungusConfigurationBuilder validBaseBlock(BlockState state) {
        validBaseState = state;
        return this;
    }

    public HugeFungusConfigurationBuilder stemState(BlockState state) {
        state = stemState;
        return this;
    }

    public HugeFungusConfigurationBuilder hatState(BlockState state) {
        hatState = state;
        return this;
    }

    public HugeFungusConfigurationBuilder decorState(BlockState state) {
        decorState = state;
        return this;
    }

    public HugeFungusConfigurationBuilder replaceableBlocks(BlockPredicate replaceable) {
        replaceableBlocks = replaceable;
        return this;
    }

    public HugeFungusConfigurationBuilder planted() {
        planted = true;
        return this;
    }

    @Override
    protected HugeFungusConfiguration createFeatureConfiguration() {
        return new HugeFungusConfiguration(
                notNull(validBaseState, "Valid base block must be defined!"),
                notNull(stemState, "Stem state must be defined!"),
                notNull(hatState, "Hat state must be defined!"),
                notNull(decorState, "Decor state must be defined!"),
                notNull(replaceableBlocks, "Replaceable blocks must be defined!"),
                planted
        );
    }

    @Override
    protected Feature<HugeFungusConfiguration> getFeature() {
        return Feature.HUGE_FUNGUS;
    }
}
