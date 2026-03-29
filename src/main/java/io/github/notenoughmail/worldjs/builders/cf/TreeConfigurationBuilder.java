package io.github.notenoughmail.worldjs.builders.cf;

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

    public TreeConfigurationBuilder trunkProvider(BlockStateProvider provider) {
        trunkProvider = provider;
        return this;
    }

    public TreeConfigurationBuilder trunkPlacer(TrunkPlacer placer) {
        trunkPlacer = placer;
        return this;
    }

    public TreeConfigurationBuilder foliageProvider(BlockStateProvider provider) {
        foliageProvider = provider;
        return this;
    }

    public TreeConfigurationBuilder foliagePlacer(FoliagePlacer placer) {
        foliagePlacer = placer;
        return this;
    }

    public TreeConfigurationBuilder rootPlacer(RootPlacer placer) {
        rootPlacer = placer;
        return this;
    }

    public TreeConfigurationBuilder dirtProvider(BlockStateProvider provider) {
        dirtProvider = provider;
        return this;
    }

    public TreeConfigurationBuilder minimumSize(FeatureSize size) {
        minimumSize = size;
        return this;
    }

    public TreeConfigurationBuilder decorators(List<TreeDecorator> decorators) {
        this.decorators = decorators;
        return this;
    }

    public TreeConfigurationBuilder ignoreVeins() {
        ignoreVines = true;
        return this;
    }

    public TreeConfigurationBuilder forceDirt() {
        forceDirt = true;
        return this;
    }

    @Override
    protected TreeConfiguration createFeatureConfiguration() {
        return Util.make(new TreeConfiguration.TreeConfigurationBuilder(
                notNull(trunkProvider, "Trunk provider must not be null!"),
                notNull(trunkPlacer, "Trunk placer must not be null!"),
                notNull(foliageProvider, "Foliage provider must not be null!"),
                notNull(foliagePlacer, "Foliage placer must not be null!"),
                Optional.ofNullable(rootPlacer),
                notNull(minimumSize, "Minimum size must not be null!")
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
