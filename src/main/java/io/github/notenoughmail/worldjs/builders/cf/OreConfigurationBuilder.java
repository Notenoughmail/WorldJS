package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

import java.util.ArrayList;
import java.util.List;

@ReturnsSelf
public class OreConfigurationBuilder extends ConfiguredFeatureBuilder<OreConfiguration> {

    public transient List<OreConfiguration.TargetBlockState> targets;
    public transient int size;
    public transient float discardChanceOnAirExposure;

    public OreConfigurationBuilder(ResourceLocation id) {
        super(id);
        targets = new ArrayList<>();
    }

    public OreConfigurationBuilder target(OreConfiguration.TargetBlockState target) {
        targets.add(target);
        return this;
    }

    public OreConfigurationBuilder size(int size) {
        this.size = assertRange(size, 0, 64, "Size must be in range [0, 64]");
        return this;
    }

    public OreConfigurationBuilder discardChanceOnAirExposure(float chance) {
        discardChanceOnAirExposure = assertUnit(chance, "Discard chance on air exposure must be in the range [0, 1]");
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

    @Override
    protected Feature<OreConfiguration> getFeature() {
        return Feature.ORE;
    }
}
