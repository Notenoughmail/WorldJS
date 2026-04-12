package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@ReturnsSelf
public class OreConfigurationBuilder extends ConfiguredFeatureBuilder.WithFeature<OreConfiguration> {

    public transient List<OreConfiguration.TargetBlockState> targets;
    public transient int size;
    public transient float discardChanceOnAirExposure;

    public OreConfigurationBuilder(ResourceLocation id, Supplier<Feature<OreConfiguration>> feature) {
        super(id, feature);
        targets = new ArrayList<>();
    }

    @Info("The targets for ore placement")
    public OreConfigurationBuilder target(OreConfiguration.TargetBlockState target) {
        targets.add(target);
        return this;
    }

    @Info("The size of the ore vein, in the range [0, 64]")
    public OreConfigurationBuilder size(int size) {
        this.size = assertRange(size, 0, 64, "size");
        return this;
    }

    @Info("The chance the ore vein is discarded if any of its blocks neighbor air, in the range [0, 1]")
    public OreConfigurationBuilder discardChanceOnAirExposure(float chance) {
        discardChanceOnAirExposure = assertUnit(chance, "discardChanceOnAirExposure");
        return this;
    }

    @Override
    protected OreConfiguration createFeatureConfiguration() {
        return new OreConfiguration(
                targets,
                size,
                discardChanceOnAirExposure
        );
    }
}
