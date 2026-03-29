package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.EndGatewayConfiguration;
import org.jetbrains.annotations.Nullable;

@ReturnsSelf
public class EndGatewayConfigurationBuilder extends ConfiguredFeatureBuilder<EndGatewayConfiguration> {

    public transient boolean exact;
    @Nullable
    public transient BlockPos exit;

    public EndGatewayConfigurationBuilder(ResourceLocation id) {
        super(id);
    }

    public EndGatewayConfigurationBuilder exact(boolean exact) {
        this.exact = exact;
        return this;
    }

    public EndGatewayConfigurationBuilder exit(BlockPos pos) {
        exit = pos;
        return this;
    }

    @Override
    protected EndGatewayConfiguration createFeatureConfiguration() {
        return exit == null ?
                EndGatewayConfiguration.delayedExitSearch() :
                EndGatewayConfiguration.knownExit(exit, exact);
    }

    @Override
    protected Feature<EndGatewayConfiguration> getFeature() {
        return Feature.END_GATEWAY;
    }
}
