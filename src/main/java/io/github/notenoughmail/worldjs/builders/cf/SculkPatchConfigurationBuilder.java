package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.SculkPatchConfiguration;

@ReturnsSelf
public class SculkPatchConfigurationBuilder extends ConfiguredFeatureBuilder<SculkPatchConfiguration> {

    public transient int chargeCount,
            amountPerCharge,
            spreadAttempts,
            growthRounds,
            spreadRounds;
    public transient IntProvider extraRareGrowths;
    public transient float catalystChance;

    public SculkPatchConfigurationBuilder(ResourceLocation id) {
        super(id);
        chargeCount = amountPerCharge = spreadAttempts = 1;
    }

    public SculkPatchConfigurationBuilder chargeCount(int count) {
        chargeCount = assertRange(count, 1, 32, "chargeCount");
        return this;
    }

    public SculkPatchConfigurationBuilder amountPerCharge(int amount) {
        amountPerCharge = assertRange(amount, 1, 500, "amountPerCharge");
        return this;
    }

    public SculkPatchConfigurationBuilder spreadAttempts(int attempts) {
        spreadAttempts = assertRange(attempts, 1, 64, "spreadAttempts");
        return this;
    }

    public SculkPatchConfigurationBuilder growthRounds(int rounds) {
        growthRounds = assertRange(rounds, 0, 8, "growthRounds");
        return this;
    }

    public SculkPatchConfigurationBuilder spreadRounds(int rounds) {
        spreadRounds = assertRange(rounds, 0, 8, "spreadRounds");
        return this;
    }

    public SculkPatchConfigurationBuilder extraRareGrowths(IntProvider provider) {
        extraRareGrowths = provider;
        return this;
    }

    public SculkPatchConfigurationBuilder catalystChance(float chance) {
        catalystChance = assertUnit(chance, "catalystChance");
        return this;
    }

    @Override
    protected SculkPatchConfiguration createFeatureConfiguration() {
        return new SculkPatchConfiguration(
                chargeCount,
                amountPerCharge,
                spreadAttempts,
                growthRounds,
                spreadRounds,
                notNull(extraRareGrowths, "extraRareGrowths"),
                catalystChance
        );
    }

    @Override
    protected Feature<SculkPatchConfiguration> getFeature() {
        return Feature.SCULK_PATCH;
    }
}
