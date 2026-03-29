package io.github.notenoughmail.worldjs.builders.cf;

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

    @Override
    protected HugeMushroomFeatureConfiguration createFeatureConfiguration() {
        return new HugeMushroomFeatureConfiguration(
                notNull(cap, "Cap provider must not be null!"),
                notNull(stem, "Stem provider must not be null!"),
                foliageRadius
        );
    }
}
