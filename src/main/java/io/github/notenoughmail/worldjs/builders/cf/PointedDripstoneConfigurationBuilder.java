package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import io.github.notenoughmail.worldjs.util.Validations;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.PointedDripstoneConfiguration;

@ReturnsSelf
public class PointedDripstoneConfigurationBuilder extends ConfiguredFeatureBuilder<PointedDripstoneConfiguration> {

    public transient float chanceOfTallerDripstone,
            chanceOfDirectionalSpread,
            chanceOfSpreadRadius2,
            chanceOfSpreadRadius3;

    public PointedDripstoneConfigurationBuilder(ResourceLocation id) {
        super(id);
        chanceOfTallerDripstone = 0.2F;
        chanceOfDirectionalSpread = 0.7F;
        chanceOfSpreadRadius2 = chanceOfSpreadRadius3 = 0.5F;
    }

    @Info("Probability of a two tall dripstone generating, in the range [0, 1]")
    public PointedDripstoneConfigurationBuilder chanceOfTallerDripstone(float chance) {
        chanceOfTallerDripstone = Validations.assertUnit(chance, "chanceOfTallerDripstone");
        return this;
    }

    @Info("Probability dripstone spreads in a horizontal direction, in the range [0, 1]")
    public PointedDripstoneConfigurationBuilder chanceOfDirectionalSpread(float chance) {
        chanceOfDirectionalSpread = Validations.assertUnit(chance, "chanceOfDirectionalSpread");
        return this;
    }

    @Info("Probability dripstone spreads horizontally two blocks, in the range [0, 1]")
    public PointedDripstoneConfigurationBuilder chanceOfSpreadRadius2(float chance) {
        chanceOfSpreadRadius2 = Validations.assertUnit(chance, "chanceOfSpreadRadius2");
        return this;
    }

    @Info("Probability dripstone spreads horizontally a third block after the second block spread, in the range [0, 1]")
    public PointedDripstoneConfigurationBuilder chanceOfSpreadRadius3(float chance) {
        chanceOfSpreadRadius3 = Validations.assertUnit(chance, "chanceOfSpreadRadius3");
        return this;
    }

    @Override
    protected PointedDripstoneConfiguration createFeatureConfiguration() {
        return new PointedDripstoneConfiguration(
                chanceOfTallerDripstone,
                chanceOfDirectionalSpread,
                chanceOfSpreadRadius3,
                chanceOfSpreadRadius3
        );
    }

    @Override
    protected Feature<PointedDripstoneConfiguration> getFeature() {
        return Feature.POINTED_DRIPSTONE;
    }
}
