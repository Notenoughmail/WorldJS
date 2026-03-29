package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
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

    public transient Holder.Reference<PlacedFeature> treeFeature;
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

    public RootSystemConfigurationBuilder treeFeature(Holder.Reference<PlacedFeature> feature) {
        treeFeature = feature;
        return this;
    }

    public RootSystemConfigurationBuilder requiredVerticalSpaceForTree(int space) {
        requiredVerticalSpaceForTree = assertRange(space, 1, 64, "Required vertical space for tree must be in range [1, 64]");
        return this;
    }

    public RootSystemConfigurationBuilder rootRadius(int radius) {
        rootRadius = assertRange(radius, 1, 64, "Root radius must be in range [1, 64]");
        return this;
    }

    public RootSystemConfigurationBuilder rootReplaceable(TagKey<Block> replaceable) {
        rootReplaceable = replaceable;
        return this;
    }

    public RootSystemConfigurationBuilder rootStateProvider(BlockStateProvider provider) {
        rootStateProvider = provider;
        return this;
    }

    public RootSystemConfigurationBuilder rootPlacementAttempts(int attempts) {
        rootPlacementAttempts = assertRange(attempts, 1, 256, "Root placement attempts must be in range [1, 256]");
        return this;
    }

    public RootSystemConfigurationBuilder rootColumnMaxHeight(int height) {
        rootColumnMaxHeight = assertRange(height, 1, 4096, "Root column max height must be in range [1, 4096]");
        return this;
    }

    public RootSystemConfigurationBuilder hangingRootRadius(int radius) {
        hangingRootRadius = assertRange(radius, 1, 64, "Hanging root radius must be in range [1, 64]");
        return this;
    }

    public RootSystemConfigurationBuilder hangingRootsVerticalSpan(int span) {
        hangingRootsVerticalSpan = assertRange(span, 0, 16, "Hanging roots vertical span must be in range [0, 16]");
        return this;
    }

    public RootSystemConfigurationBuilder hangingRootStateProvier(BlockStateProvider provider) {
        hangingRootStateProvider = provider;
        return this;
    }

    public RootSystemConfigurationBuilder hangingRootPlacementAttempts(int attempts) {
        hangingRootPlacementAttempts = assertRange(attempts, 1, 256, "Hanging root placement attempts must be in range [1, 256]");
        return this;
    }

    public RootSystemConfigurationBuilder allowedVerticalWaterForTree(int allowed) {
        allowedVerticalWaterForTree = assertRange(allowed, 1, 64, "Allowed vertical water for tree must be in range [1, 64]");
        return this;
    }

    public RootSystemConfigurationBuilder allowedTreePosition(BlockPredicate allowed) {
        allowedTreePosition = allowed;
        return this;
    }

    @Override
    protected RootSystemConfiguration createFeatureConfiguration() {
        return new RootSystemConfiguration(
                notNull(treeFeature, "Tree feature must be defined!"),
                requiredVerticalSpaceForTree,
                rootRadius,
                notNull(rootReplaceable, "Root replaceable must be defined!"),
                notNull(rootStateProvider, "Root state provider must be defined!"),
                rootPlacementAttempts,
                rootColumnMaxHeight,
                hangingRootRadius,
                hangingRootsVerticalSpan,
                notNull(hangingRootStateProvider, "Hanging root state provider must be defined!"),
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
