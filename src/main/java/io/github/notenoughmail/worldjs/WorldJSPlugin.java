package io.github.notenoughmail.worldjs;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.BuilderTypeRegistry;
import dev.latvian.mods.kubejs.util.Cast;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import io.github.notenoughmail.worldjs.builders.base.PlacedFeatureBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class WorldJSPlugin implements KubeJSPlugin {

    @Override
    public void registerBuilderTypes(BuilderTypeRegistry registry) {
        registry.addDefault(Registries.PLACED_FEATURE, PlacedFeatureBuilder.class, PlacedFeatureBuilder::new);
        registry.of(Registries.CONFIGURED_FEATURE, c -> {
            add(c, "no_op", Feature.NO_OP);
            add(c, "chorus_plant", Feature.CHORUS_PLANT);
            add(c, "void_start_platform", Feature.VOID_START_PLATFORM);
            add(c, "desert_well", Feature.DESERT_WELL);
            add(c, "ice_spike", Feature.ICE_SPIKE);
            add(c, "glowstone_blob", Feature.GLOWSTONE_BLOB);
            add(c, "freeze_top_later", Feature.FREEZE_TOP_LAYER);
            add(c, "vines", Feature.VINES);
            add(c, "monster_room", Feature.MONSTER_ROOM);
            add(c, "blue_ice", Feature.BLUE_ICE);
            add(c, "end_platform", Feature.END_PLATFORM);
            add(c, "end_island", Feature.END_ISLAND);
            add(c, "kelp", Feature.KELP);
            add(c, "coral_tree", Feature.CORAL_TREE);
            add(c, "coral_mushroom", Feature.CORAL_MUSHROOM);
            add(c, "coral_claw", Feature.CORAL_CLAW);
            add(c, "weeping_vines", Feature.WEEPING_VINES);
            add(c, "basalt_pillar", Feature.BASALT_PILLAR);
            // TODO: 1.0.0 | All the rest...
        });
    }

    private static <F extends Feature<NoneFeatureConfiguration>> void add(BuilderTypeRegistry.Callback<ConfiguredFeature<?, ?>> callback, String name, F feature) {
        callback.add(KubeJS.id(name), Cast.to(ConfiguredFeatureBuilder.class), ConfiguredFeatureBuilder.NoneConfig.factory(feature));
    }
}
