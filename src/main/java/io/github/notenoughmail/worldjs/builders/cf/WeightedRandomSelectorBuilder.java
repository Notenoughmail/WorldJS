package io.github.notenoughmail.worldjs.builders.cf;

import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.WorldJS;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import io.github.notenoughmail.worldjs.types.features.WeightedRandomSelectorFeature;
import io.github.notenoughmail.worldjs.util.WeightedValue;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;

@ReturnsSelf
public class WeightedRandomSelectorBuilder extends ConfiguredFeatureBuilder.WithFeature<WeightedRandomSelectorFeature.Configuration> {

    public transient SimpleWeightedRandomList<Holder<PlacedFeature>> features = SimpleWeightedRandomList.empty();

    public WeightedRandomSelectorBuilder(ResourceLocation id) {
        super(id, WorldJS.WEIGHTED_RANDOM_SELECTOR);
    }

    public WeightedRandomSelectorBuilder features(List<WeightedValue<Holder.Reference<PlacedFeature>>> features) {
        this.features = WeightedValue.toVanilla(features);
        return this;
    }

    @Override
    protected WeightedRandomSelectorFeature.Configuration createFeatureConfiguration() {
        return new WeightedRandomSelectorFeature.Configuration(
                validate(features, f -> f.isEmpty() ? "'features' must not be empty!" : null)
        );
    }
}
