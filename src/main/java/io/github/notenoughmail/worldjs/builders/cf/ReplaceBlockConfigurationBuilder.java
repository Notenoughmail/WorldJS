package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;

import java.util.List;

@ReturnsSelf
public class ReplaceBlockConfigurationBuilder extends ConfiguredFeatureBuilder<ReplaceBlockConfiguration> {

    public transient List<OreConfiguration.TargetBlockState> targets;

    public ReplaceBlockConfigurationBuilder(ResourceLocation id) {
        super(id);
        targets = List.of();
    }

    @Info("The replacement targets")
    public ReplaceBlockConfigurationBuilder targetStates(List<OreConfiguration.TargetBlockState> targets) {
        this.targets = targets;
        return this;
    }

    @Override
    protected ReplaceBlockConfiguration createFeatureConfiguration() {
        return new ReplaceBlockConfiguration(targets);
    }

    @Override
    protected Feature<ReplaceBlockConfiguration> getFeature() {
        return Feature.REPLACE_SINGLE_BLOCK;
    }
}
