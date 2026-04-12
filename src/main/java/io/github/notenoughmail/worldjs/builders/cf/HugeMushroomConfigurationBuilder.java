package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.function.Supplier;

@ReturnsSelf
public class HugeMushroomConfigurationBuilder extends ConfiguredFeatureBuilder.WithFeature<HugeMushroomFeatureConfiguration> {

    public transient BlockStateProvider cap;
    public transient BlockStateProvider stem;
    public transient int foliageRadius;

    public HugeMushroomConfigurationBuilder(ResourceLocation id, Supplier<Feature<HugeMushroomFeatureConfiguration>> feature) {
        super(id, feature);
        foliageRadius = 2;
    }

    @Info("The block to use for the cap")
    public HugeMushroomConfigurationBuilder capProvider(BlockStateProvider provider) {
        cap = provider;
        return this;
    }

    @Info("The block to use for the stem")
    public HugeMushroomConfigurationBuilder stemProvider(BlockStateProvider provider) {
        stem = provider;
        return this;
    }

    @Info("The size of the cap")
    public HugeMushroomConfigurationBuilder foliageRadius(int radius) {
        foliageRadius = radius;
        return this;
    }

    @Override
    protected HugeMushroomFeatureConfiguration createFeatureConfiguration() {
        return new HugeMushroomFeatureConfiguration(
                notNull(cap, "capProvider"),
                notNull(stem, "stemProvider"),
                foliageRadius
        );
    }
}
