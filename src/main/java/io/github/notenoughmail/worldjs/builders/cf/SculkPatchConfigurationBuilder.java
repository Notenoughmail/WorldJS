package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.typings.Info;
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
        chargeCount
                = amountPerCharge
                = spreadAttempts
                = growthRounds
                = 1;
    }

    @Info("The number of charges, in the range [1, 32]")
    public SculkPatchConfigurationBuilder chargeCount(int count) {
        chargeCount = assertRange(count, 1, 32, "chargeCount");
        return this;
    }

    @Info("The initial value of each charge, in the range [1, 500]")
    public SculkPatchConfigurationBuilder amountPerCharge(int amount) {
        amountPerCharge = assertRange(amount, 1, 500, "amountPerCharge");
        return this;
    }

    @Info("The number of attempts to spread, in the range [1, 64]")
    public SculkPatchConfigurationBuilder spreadAttempts(int attempts) {
        spreadAttempts = assertRange(attempts, 1, 64, "spreadAttempts");
        return this;
    }

    @Info("The number of times to generate, in the range [0, 8]")
    public SculkPatchConfigurationBuilder growthRounds(int rounds) {
        growthRounds = assertRange(rounds, 0, 8, "growthRounds");
        return this;
    }

    @Info("The number of times to spread, in the range [0, 8]")
    public SculkPatchConfigurationBuilder spreadRounds(int rounds) {
        spreadRounds = assertRange(rounds, 0, 8, "spreadRounds");
        return this;
    }

    @Info("The number of extra shriekers generated")
    public SculkPatchConfigurationBuilder extraRareGrowths(IntProvider provider) {
        extraRareGrowths = provider;
        return this;
    }

    @Info("The chance of generating a catalyst, in the range [0, 1]")
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
