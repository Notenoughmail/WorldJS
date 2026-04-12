package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SpikeConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SpikeConfigurationBuilder extends ConfiguredFeatureBuilder<SpikeConfiguration> {

    public transient boolean invulnerable;
    public transient List<SpikeFeature.EndSpike> spikes;
    @Nullable
    public transient BlockPos target;

    public SpikeConfigurationBuilder(ResourceLocation id) {
        super(id);
        spikes = new ArrayList<>();
    }

    @Info("Makes the placed crystals invulnerable")
    public SpikeConfigurationBuilder invulnerableCrystals() {
        invulnerable = true;
        return this;
    }

    @Info("The position the crystal beams target")
    public SpikeConfigurationBuilder crystalBeamTarget(BlockPos pos) {
        target = pos;
        return this;
    }

    @Info(
            value = "Add a spike",
            params = {
                    @Param(name = "centerX", value = "The x coordinate"),
                    @Param(name = "centerZ", value = "The z coordinate"),
                    @Param(name = "radius", value = "The radius of the spike"),
                    @Param(name = "height", value = "The height of the spike"),
                    @Param(name = "guarded", value = "If the crystal on the spike should have an iron-bar cage")
            }
    )
    public SpikeConfigurationBuilder spike(int centerX, int centerZ, int radius, int height, boolean guarded) {
        spikes.add(new SpikeFeature.EndSpike(centerX, centerZ, radius, height, guarded));
        return this;
    }

    @Override
    protected SpikeConfiguration createFeatureConfiguration() {
        return new SpikeConfiguration(
                invulnerable,
                spikes,
                target
        );
    }

    @Override
    protected Feature<SpikeConfiguration> getFeature() {
        return Feature.END_SPIKE;
    }
}
