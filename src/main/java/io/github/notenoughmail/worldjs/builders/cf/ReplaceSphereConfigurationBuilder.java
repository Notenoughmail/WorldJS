package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceSphereConfiguration;

// TODO: 1.0.0 | JSDoc
@ReturnsSelf
public class ReplaceSphereConfigurationBuilder extends ConfiguredFeatureBuilder<ReplaceSphereConfiguration> {

    public transient BlockState target, replace;
    public transient IntProvider radius;

    public ReplaceSphereConfigurationBuilder(ResourceLocation id) {
        super(id);
        target = replace = Blocks.AIR.defaultBlockState();
    }

    public ReplaceSphereConfigurationBuilder target(BlockState state) {
        target = state;
        return this;
    }

    public ReplaceSphereConfigurationBuilder state(BlockState state) {
        replace = state;
        return this;
    }

    public ReplaceSphereConfigurationBuilder radius(IntProvider provider) {
        radius = assertRange(provider, 0, 12, "radius");
        return this;
    }

    @Override
    protected ReplaceSphereConfiguration createFeatureConfiguration() {
        return new ReplaceSphereConfiguration(
                target,
                replace,
                notNull(radius, "radius")
        );
    }

    @Override
    protected Feature<ReplaceSphereConfiguration> getFeature() {
        return Feature.REPLACE_BLOBS;
    }
}
