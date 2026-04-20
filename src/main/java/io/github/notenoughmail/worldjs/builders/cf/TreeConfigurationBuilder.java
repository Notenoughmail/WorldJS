package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@ReturnsSelf
public class TreeConfigurationBuilder extends ConfiguredFeatureBuilder<TreeConfiguration> {

    public transient BlockStateProvider trunkProvider;
    public transient TrunkPlacer trunkPlacer;
    public transient BlockStateProvider foliageProvider;
    public transient FoliagePlacer foliagePlacer;
    @Nullable
    public transient RootPlacer rootPlacer;
    @Nullable
    public transient BlockStateProvider dirtProvider;
    public transient FeatureSize minimumSize;
    public transient List<TreeDecorator> decorators;
    public transient boolean ignoreVines;
    public transient boolean forceDirt;

    public TreeConfigurationBuilder(ResourceLocation id) {
        super(id);
        ignoreVines = forceDirt = false;
        decorators = List.of();
    }

    @Info("The block to use as the trunk")
    public TreeConfigurationBuilder trunkProvider(BlockStateProvider provider) {
        trunkProvider = provider;
        return this;
    }

    @Info("The trunk placer, defines how the trunk is generated")
    public TreeConfigurationBuilder trunkPlacer(TrunkPlacer placer) {
        trunkPlacer = placer;
        return this;
    }

    @Info("The block to use for foliage")
    public TreeConfigurationBuilder foliageProvider(BlockStateProvider provider) {
        foliageProvider = provider;
        return this;
    }

    @Info("The foliage placer, defines how the foliage is generated")
    public TreeConfigurationBuilder foliagePlacer(FoliagePlacer placer) {
        foliagePlacer = placer;
        return this;
    }

    @Info("The root placer, defines the root blocks nad how they generate")
    public TreeConfigurationBuilder rootPlacer(RootPlacer placer) {
        rootPlacer = placer;
        return this;
    }

    @Info("The dirt/ground blocks to place below the tree")
    public TreeConfigurationBuilder dirtProvider(BlockStateProvider provider) {
        dirtProvider = provider;
        return this;
    }

    @Info("The minimum size of the tree")
    public TreeConfigurationBuilder minimumSize(FeatureSize size) {
        minimumSize = size;
        return this;
    }

    @Info("Special decorators that can be added to the tree")
    public TreeConfigurationBuilder decorators(List<TreeDecorator> decorators) {
        this.decorators = decorators;
        return this;
    }

    @Info("Allow the tree to generate if vines are blocking the spawn position")
    public TreeConfigurationBuilder ignoreVines() {
        ignoreVines = true;
        return this;
    }

    @Info("Forces the dirt blocks to be placed below the tree, even if existing blocks were already dirt-like")
    public TreeConfigurationBuilder forceDirt() {
        forceDirt = true;
        return this;
    }

    @Override
    protected TreeConfiguration createFeatureConfiguration() {
        return Util.make(new TreeConfiguration.TreeConfigurationBuilder(
                notNull(trunkProvider, "trunkProvider"),
                notNull(trunkPlacer, "trunkPlacer"),
                notNull(foliageProvider, "foliageProvider"),
                notNull(foliagePlacer, "foliagePlacer"),
                Optional.ofNullable(rootPlacer),
                notNull(minimumSize, "minimumSize")
        ), b -> {
            if (ignoreVines) b.ignoreVines();
            if (forceDirt) b.forceDirt();
            if (!decorators.isEmpty()) b.decorators(decorators);
            if (dirtProvider != null) b.dirt(dirtProvider);
        }).build();
    }

    @Override
    protected Feature<TreeConfiguration> getFeature() {
        return Feature.TREE;
    }
}
