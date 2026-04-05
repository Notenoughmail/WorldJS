package io.github.notenoughmail.worldjs.builders.cf;

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

    public OreConfigurationBuilder target(OreConfiguration.TargetBlockState target) {
        targets.add(target);
        return this;
    }

    public OreConfigurationBuilder size(int size) {
        this.size = assertRange(size, 0, 64, "size");
        return this;
    }

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
