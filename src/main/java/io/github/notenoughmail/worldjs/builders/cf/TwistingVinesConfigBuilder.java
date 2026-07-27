package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import io.github.notenoughmail.worldjs.util.Validations;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TwistingVinesConfig;

@ReturnsSelf
public class TwistingVinesConfigBuilder extends ConfiguredFeatureBuilder<TwistingVinesConfig> {

    public transient int width, height, maxHeight;

    public TwistingVinesConfigBuilder(ResourceLocation id) {
        super(id);
        width = height = maxHeight = 1;
    }

    @Info("The max spread width, must be positive")
    public TwistingVinesConfigBuilder spreadWidth(int width) {
        this.width = Validations.assertPositive(width, "Spread width must be positive");
        return this;
    }

    @Info("The max spread height, must be positive")
    public TwistingVinesConfigBuilder spreadHeight(int height) {
        this.height = Validations.assertPositive(height, "Spread height must be positive");
        return this;
    }

    @Info("The max height, must be positive")
    public TwistingVinesConfigBuilder maxHeight(int height) {
        maxHeight = Validations.assertPositive(height, "Max height must be positive");
        return this;
    }

    @Override
    protected TwistingVinesConfig createFeatureConfiguration() {
        return new TwistingVinesConfig(
                width,
                height,
                maxHeight
        );
    }

    @Override
    protected Feature<TwistingVinesConfig> getFeature() {
        return Feature.TWISTING_VINES;
    }
}
