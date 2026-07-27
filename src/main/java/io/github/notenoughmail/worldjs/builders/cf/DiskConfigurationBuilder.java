package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import io.github.notenoughmail.worldjs.util.Validations;
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

    @Info("The block to place")
    public DiskConfigurationBuilder stateProvider(BlockStateProvider provider) {
        stateProvider = RuleBasedBlockStateProvider.simple(provider);
        return this;
    }

    @Info(
            value = "The block to place, depending on rules and with a fallback value",
            params = {
                    @Param(name = "fallback", value = "The bloc kto place if no rules pass"),
                    @Param(name = "rules", value = "The rules for what block to place")
            }
    )
    public DiskConfigurationBuilder stateProvider(BlockStateProvider fallback, List<RuleBasedBlockStateProvider.Rule> rules) {
        stateProvider = new RuleBasedBlockStateProvider(fallback, rules);
        return this;
    }

    @Info("The validator for placement of the feature")
    public DiskConfigurationBuilder target(BlockPredicate target) {
        this.target = target;
        return this;
    }

    @Info("The radius of the disk, in the range [0, 8]")
    public DiskConfigurationBuilder radius(IntProvider radius) {
        this.radius = Validations.assertRange(radius, 0, 8, "radius");
        return this;
    }

    @Info("Half the height of the disk, in the range [0, 4]")
    public DiskConfigurationBuilder halfHeight(int height) {
        halfHeight = Validations.assertRange(height, 0, 4, "halfHeight");
        return this;
    }

    @Override
    protected DiskConfiguration createFeatureConfiguration() {
        return new DiskConfiguration(
                notNull(stateProvider, "stateProvider"),
                notNull(target, "target"),
                notNull(radius, "radius"),
                halfHeight
        );
    }

    @Override
    protected Feature<DiskConfiguration> getFeature() {
        return Feature.DISK;
    }
}
