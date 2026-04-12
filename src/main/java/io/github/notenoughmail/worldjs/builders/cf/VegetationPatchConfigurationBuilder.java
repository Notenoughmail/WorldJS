package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
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

// TODO: 1.0.0 | JSDoc
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

    public VegetationPatchConfigurationBuilder replaceableBlocks(TagKey<Block> blocks) {
        replaceable = blocks;
        return this;
    }

    public VegetationPatchConfigurationBuilder groundState(BlockStateProvider state) {
        groundState = state;
        return this;
    }

    public VegetationPatchConfigurationBuilder vegetationFeature(Holder.Reference<PlacedFeature> feature) {
        vegetationFeature = feature;
        return this;
    }

    public VegetationPatchConfigurationBuilder surface(CaveSurface surface) {
        this.surface = surface;
        return this;
    }

    public VegetationPatchConfigurationBuilder depth(IntProvider depth) {
        this.depth = assertRange(depth, 1, 128, "depth");
        return this;
    }

    public VegetationPatchConfigurationBuilder extraBottomBlockChance(float chance) {
        extraBottomBlockChance = assertUnit(chance, "extraBottomBlockChance");
        return this;
    }

    public VegetationPatchConfigurationBuilder verticalRange(int range) {
        verticalRange = assertRange(range, 1, 256, "verticalRange");
        return this;
    }

    public VegetationPatchConfigurationBuilder vegetationChance(float chance) {
        vegetationChance = assertUnit(chance, "vegetationChance");
        return this;
    }

    public VegetationPatchConfigurationBuilder xzRange(IntProvider range) {
        xzRadius = range;
        return this;
    }

    public VegetationPatchConfigurationBuilder extraEdgeColumnChance(float chance) {
        extraEdgeColumnChance = assertUnit(chance, "extraEdgeColumnChance");
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
