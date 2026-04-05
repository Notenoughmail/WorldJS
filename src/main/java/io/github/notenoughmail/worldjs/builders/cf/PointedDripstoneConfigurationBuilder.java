package io.github.notenoughmail.worldjs.builders.cf;

import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.PointedDripstoneConfiguration;

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

    public PointedDripstoneConfigurationBuilder chanceOfTallerDripstone(float chance) {
        chanceOfTallerDripstone = assertUnit(chance, "chanceOfTallerDripstone");
        return this;
    }

    public PointedDripstoneConfigurationBuilder chanceOfDirectionalSpread(float chance) {
        chanceOfDirectionalSpread = assertUnit(chance, "chanceOfDirectionalSpread");
        return this;
    }

    public PointedDripstoneConfigurationBuilder chanceOfSpreadRadius2(float chance) {
        chanceOfSpreadRadius2 = assertUnit(chance, "chanceOfSpreadRadius2");
        return this;
    }

    public PointedDripstoneConfigurationBuilder chanceOfSpreadRadius3(float chance) {
        chanceOfSpreadRadius3 = assertUnit(chance, "chanceOfSpreadRadius3");
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
