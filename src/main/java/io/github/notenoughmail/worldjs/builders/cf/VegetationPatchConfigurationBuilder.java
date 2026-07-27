package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import io.github.notenoughmail.worldjs.util.Validations;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.function.Supplier;

@ReturnsSelf
public class VegetationPatchConfigurationBuilder extends ConfiguredFeatureBuilder.WithFeature<VegetationPatchConfiguration> {

    public transient TagKey<Block> replaceable;
    public transient BlockStateProvider groundState;
    public transient Holder.Reference<PlacedFeature> vegetationFeature;
    public transient CaveSurface surface;
    public transient IntProvider depth;
    public transient float extraBottomBlockChance;
    public transient int verticalRange;
    public transient float vegetationChance;
    public transient IntProvider xzRadius;
    public transient float extraEdgeColumnChance;

    public VegetationPatchConfigurationBuilder(ResourceLocation id, Supplier<Feature<VegetationPatchConfiguration>> feature) {
        super(id, feature);
        surface = CaveSurface.FLOOR;
        extraBottomBlockChance = 0;
        verticalRange = 1;
        vegetationChance = 0;
        extraEdgeColumnChance = 0;
    }

    @Info("The blocks that can be replaced with vegetation blocks")
    public VegetationPatchConfigurationBuilder replaceableBlocks(TagKey<Block> blocks) {
        replaceable = blocks;
        return this;
    }

    @Info("The block used for generating the column")
    public VegetationPatchConfigurationBuilder groundState(BlockStateProvider state) {
        groundState = state;
        return this;
    }

    @Info("The feature to place on finding a valid position")
    public VegetationPatchConfigurationBuilder vegetationFeature(Holder.Reference<PlacedFeature> feature) {
        vegetationFeature = feature;
        return this;
    }

    @Info("The surface to place on")
    public VegetationPatchConfigurationBuilder surface(CaveSurface surface) {
        this.surface = surface;
        return this;
    }

    @Info("The amount of blocks that should be replaced by the column, in the range [1, 128]")
    public VegetationPatchConfigurationBuilder depth(IntProvider depth) {
        this.depth = Validations.assertRange(depth, 1, 128, "depth");
        return this;
    }

    @Info("The chance to add an extra block to the height, in the range [0, 1]")
    public VegetationPatchConfigurationBuilder extraBottomBlockChance(float chance) {
        extraBottomBlockChance = Validations.assertUnit(chance, "extraBottomBlockChance");
        return this;
    }

    @Info("The y radius the column should search in for available placement, in the range [1, 256]")
    public VegetationPatchConfigurationBuilder verticalRange(int range) {
        verticalRange = Validations.assertRange(range, 1, 256, "verticalRange");
        return this;
    }

    @Info("The chance of placing the vegetation feature on finding a valid position, in the range [0, 1]")
    public VegetationPatchConfigurationBuilder vegetationChance(float chance) {
        vegetationChance = Validations.assertUnit(chance, "vegetationChance");
        return this;
    }

    @Info("The radius to search for valid positions in the x and z directions")
    public VegetationPatchConfigurationBuilder xzRadius(IntProvider radius) {
        xzRadius = radius;
        return this;
    }

    @Info("The chance to add a search position adjacent next to the initial rectangle, in the range [0, 1]")
    public VegetationPatchConfigurationBuilder extraEdgeColumnChance(float chance) {
        extraEdgeColumnChance = Validations.assertUnit(chance, "extraEdgeColumnChance");
        return this;
    }

    @Override
    protected VegetationPatchConfiguration createFeatureConfiguration() {
        return new VegetationPatchConfiguration(
                notNull(replaceable, "replaceableBlocks"),
                notNull(groundState, "groundState"),
                notNull(vegetationFeature, "vegetationFeature"),
                surface,
                notNull(depth, "depth"),
                extraBottomBlockChance,
                verticalRange,
                vegetationChance,
                notNull(xzRadius, "xzRadius"),
                extraEdgeColumnChance
        );
    }
}
