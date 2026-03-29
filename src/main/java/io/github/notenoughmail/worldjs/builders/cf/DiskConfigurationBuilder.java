package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedBlockStateProvider;

import java.util.List;

@ReturnsSelf
public class DiskConfigurationBuilder extends ConfiguredFeatureBuilder<DiskConfiguration> {

    public transient RuleBasedBlockStateProvider stateProvider;
    public transient BlockPredicate target;
    public transient IntProvider radius;
    public transient  int halfHeight;

    public DiskConfigurationBuilder(ResourceLocation id) {
        super(id);
    }

    public DiskConfigurationBuilder stateProvider(BlockStateProvider provider) {
        stateProvider = RuleBasedBlockStateProvider.simple(provider);
        return this;
    }

    public DiskConfigurationBuilder stateProvider(BlockStateProvider fallback, List<RuleBasedBlockStateProvider.Rule> rules) {
        stateProvider = new RuleBasedBlockStateProvider(fallback, rules);
        return this;
    }

    public DiskConfigurationBuilder target(BlockPredicate target) {
        this.target = target;
        return this;
    }

    public DiskConfigurationBuilder radius(IntProvider radius) {
        this.radius = assertRange(radius, 0, 8, "Radius must be in the range [0, 8]");
        return this;
    }

    public DiskConfigurationBuilder halfHeight(int height) {
        halfHeight = assertRange(height, 0, 4, "Half height must be in the range [0, 4]");
        return this;
    }

    @Override
    protected DiskConfiguration createFeatureConfiguration() {
        return new DiskConfiguration(
                notNull(stateProvider, "State provider must be defined!"),
                notNull(target, "Target must be defined!"),
                notNull(radius, "Radius must be defined!"),
                halfHeight
        );
    }

    @Override
    protected Feature<DiskConfiguration> getFeature() {
        return Feature.DISK;
    }
}
