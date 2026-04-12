package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.MultifaceGrowthConfiguration;

@ReturnsSelf
public class MultifaceGrowthConfigurationBuilder extends ConfiguredFeatureBuilder<MultifaceGrowthConfiguration> {

    public transient MultifaceBlock block;
    public transient int searchRange;
    public transient boolean placeOnFloor, placeOnCeiling, placeOnWall;
    public transient float chanceOfSpreading;
    public transient HolderSet<Block> canBePlacedOn;

    public MultifaceGrowthConfigurationBuilder(ResourceLocation id) {
        super(id);
        block = Cast.to(Blocks.GLOW_LICHEN);
        searchRange = 1;
        canBePlacedOn = HolderSet.empty();
    }

    @Info("The block to place. Must be an instance of `MultifaceBlock`")
    public MultifaceGrowthConfigurationBuilder block(Block block) {
        if (block instanceof MultifaceBlock m) {
            this.block = m;
            return this;
        }
        throw new IllegalArgumentException("Block must be an instance of MultifaceBlock!");
    }

    @Info("The range to search in to place, in the range [1, 64]")
    public MultifaceGrowthConfigurationBuilder searchRange(int range) {
        searchRange = assertRange(range, 1, 64, "searchRange");
        return this;
    }

    @Info("Allows the feature to place on the floor")
    public MultifaceGrowthConfigurationBuilder canPlaceOnFloor() {
        placeOnFloor = true;
        return this;
    }

    @Info("Allows the feature to place on the ceiling")
    public MultifaceGrowthConfigurationBuilder canPlaceOnCeiling() {
        placeOnCeiling = true;
        return this;
    }

    @Info("Allows the feature to place on the wall")
    public MultifaceGrowthConfigurationBuilder canPlaceOnWall() {
        placeOnWall = true;
        return this;
    }

    @Info("The chance the feature spreads, in the range [0, 1]")
    public MultifaceGrowthConfigurationBuilder chanceOfSpreading(float chance) {
        chanceOfSpreading = assertUnit(chance, "chanceOfSpreading");
        return this;
    }

    @Info("The block(s) the placed blcok can be placed on")
    public MultifaceGrowthConfigurationBuilder canBePlacedOn(HolderSet<Block> blocks) {
        canBePlacedOn = blocks;
        return this;
    }

    @Override
    protected MultifaceGrowthConfiguration createFeatureConfiguration() {
        return new MultifaceGrowthConfiguration(
                block,
                searchRange,
                placeOnFloor,
                placeOnCeiling,
                placeOnWall,
                chanceOfSpreading,
                canBePlacedOn
        );
    }

    @Override
    protected Feature<MultifaceGrowthConfiguration> getFeature() {
        return Feature.MULTIFACE_GROWTH;
    }
}
