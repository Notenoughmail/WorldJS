package io.github.notenoughmail.worldjs.types.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.stream.Stream;

public class WeightedRandomSelectorFeature extends Feature<WeightedRandomSelectorFeature.Configuration> {

    public WeightedRandomSelectorFeature(Codec<Configuration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Configuration> context) {
        final RandomSource randomSource = context.random();
        final Configuration config = context.config();
        final Holder<PlacedFeature> feature = config.features.getRandomValue(randomSource).orElseThrow(IllegalStateException::new);
        return feature.value().place(
                context.level(),
                context.chunkGenerator(),
                randomSource,
                context.origin()
        );
    }

    public record Configuration(SimpleWeightedRandomList<Holder<PlacedFeature>> features) implements FeatureConfiguration {
        public static final Codec<Configuration> CODEC = SimpleWeightedRandomList.wrappedCodec(PlacedFeature.CODEC).xmap(Configuration::new, Configuration::features);

        @Override
        public Stream<ConfiguredFeature<?, ?>> getFeatures() {
            return features.unwrap().stream().flatMap(w -> w.data().value().getFeatures());
        }
    }
}
