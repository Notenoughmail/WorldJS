package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import io.github.notenoughmail.worldjs.util.Validations;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RootSystemConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

@ReturnsSelf
public class RootSystemConfigurationBuilder extends ConfiguredFeatureBuilder<RootSystemConfiguration> {

    public transient Holder.Reference<PlacedFeature> feature;
    public transient int requiredVerticalSpaceForTree, rootRadius, rootPlacementAttempts, rootColumnMaxHeight,
            hangingRootRadius, hangingRootsVerticalSpan, hangingRootPlacementAttempts, allowedVerticalWaterForTree;
    public transient TagKey<Block> rootReplaceable;
    public transient BlockStateProvider rootStateProvider, hangingRootStateProvider;
    public transient BlockPredicate allowedTreePosition;

    public RootSystemConfigurationBuilder(ResourceLocation id) {
        super(id);
        allowedTreePosition = BlockPredicate.alwaysTrue();
        requiredVerticalSpaceForTree = rootRadius  = rootPlacementAttempts = rootColumnMaxHeight
                = hangingRootPlacementAttempts = allowedVerticalWaterForTree = 1;
    }

    @Info("The feature to place on top of the root system")
    public RootSystemConfigurationBuilder feature(Holder.Reference<PlacedFeature> feature) {
        this.feature = feature;
        return this;
    }

    @Info("The vertical space required for the tree feature to generate, in the range [1, 64]")
    public RootSystemConfigurationBuilder requiredVerticalSpaceForTree(int space) {
        requiredVerticalSpaceForTree = Validations.assertRange(space, 1, 64, "requiredVerticalSpaceForTree");
        return this;
    }

    @Info("The radius to place roots in, in the range [1, 64]")
    public RootSystemConfigurationBuilder rootRadius(int radius) {
        rootRadius = Validations.assertRange(radius, 1, 64, "rootRadius");
        return this;
    }

    @Info("The blocks that can be replaced by roots")
    public RootSystemConfigurationBuilder rootReplaceable(TagKey<Block> replaceable) {
        rootReplaceable = replaceable;
        return this;
    }

    @Info("The block to use for the root blob")
    public RootSystemConfigurationBuilder rootStateProvider(BlockStateProvider provider) {
        rootStateProvider = provider;
        return this;
    }

    @Info("The number of times to try to place the root blocks, in the range [1, 256]")
    public RootSystemConfigurationBuilder rootPlacementAttempts(int attempts) {
        rootPlacementAttempts = Validations.assertRange(attempts, 1, 256, "rootPlacementAttempts");
        return this;
    }

    @Info("The maximum height of the root column, in the range [1, 4096]")
    public RootSystemConfigurationBuilder rootColumnMaxHeight(int height) {
        rootColumnMaxHeight = Validations.assertRange(height, 1, 4096, "rootColumnMaxHeight");
        return this;
    }

    @Info("The radius at which to place hanging roots, in the range [1, 64]")
    public RootSystemConfigurationBuilder hangingRootRadius(int radius) {
        hangingRootRadius = Validations.assertRange(radius, 1, 64, "hangingRootRadius");
        return this;
    }

    @Info("The vertical range over which to place hanging roots, in the range [0, 16]")
    public RootSystemConfigurationBuilder hangingRootsVerticalSpan(int span) {
        hangingRootsVerticalSpan = Validations.assertRange(span, 0, 16, "hangingRootsVerticalSpan");
        return this;
    }

    @Info("The block to use for blocks which hang below the main root blob")
    public RootSystemConfigurationBuilder hangingRootStateProvider(BlockStateProvider provider) {
        hangingRootStateProvider = provider;
        return this;
    }

    @Info("The number of times to try to place the hanging roots, in the range [1, 256]")
    public RootSystemConfigurationBuilder hangingRootPlacementAttempts(int attempts) {
        hangingRootPlacementAttempts = Validations.assertRange(attempts, 1, 256, "hangingRootPlacementAttempts");
        return this;
    }

    @Info("The maximum allowable submerged height the tree may be in, in the range [1, 64]")
    public RootSystemConfigurationBuilder allowedVerticalWaterForTree(int allowed) {
        allowedVerticalWaterForTree = Validations.assertRange(allowed, 1, 64, "allowedVerticalWaterForTree");
        return this;
    }

    @Info("The validator for the tree's position")
    public RootSystemConfigurationBuilder allowedTreePosition(BlockPredicate allowed) {
        allowedTreePosition = allowed;
        return this;
    }

    @Override
    protected RootSystemConfiguration createFeatureConfiguration() {
        return new RootSystemConfiguration(
                notNull(feature, "feature"),
                requiredVerticalSpaceForTree,
                rootRadius,
                notNull(rootReplaceable, "rootReplaceable"),
                notNull(rootStateProvider, "rootStateProvider"),
                rootPlacementAttempts,
                rootColumnMaxHeight,
                hangingRootRadius,
                hangingRootsVerticalSpan,
                notNull(hangingRootStateProvider, "hangingRootStateProvider"),
                hangingRootPlacementAttempts,
                allowedVerticalWaterForTree,
                allowedTreePosition
        );
    }

    @Override
    protected Feature<RootSystemConfiguration> getFeature() {
        return Feature.ROOT_SYSTEM;
    }
}
