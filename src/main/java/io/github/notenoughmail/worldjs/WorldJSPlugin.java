package io.github.notenoughmail.worldjs;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.BuilderFactory;
import dev.latvian.mods.kubejs.registry.BuilderTypeRegistry;
import dev.latvian.mods.kubejs.registry.ServerRegistryRegistry;
import dev.latvian.mods.kubejs.script.RecordDefaultsRegistry;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import dev.latvian.mods.rhino.type.TypeInfo;
import io.github.notenoughmail.worldjs.builders.base.BiomeModifierBuilder;
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import io.github.notenoughmail.worldjs.builders.base.PlacedFeatureBuilder;
import io.github.notenoughmail.worldjs.builders.bm.*;
import io.github.notenoughmail.worldjs.builders.cf.FossilConfigurationBuilder;
import io.github.notenoughmail.worldjs.builders.cf.RandomPatchBuilder;
import io.github.notenoughmail.worldjs.builders.cf.SpringConfigurationBuilder;
import io.github.notenoughmail.worldjs.util.WeightedValue;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Function;

public class WorldJSPlugin implements KubeJSPlugin {

    @Override
    public void registerBuilderTypes(BuilderTypeRegistry registry) {
        registry.addDefault(Registries.PLACED_FEATURE, PlacedFeatureBuilder.class, PlacedFeatureBuilder::new);
        registry.of(Registries.CONFIGURED_FEATURE, c -> {
            cf(c, "no_op", Feature.NO_OP);
            cf(c, "chorus_plant", Feature.CHORUS_PLANT);
            cf(c, "void_start_platform", Feature.VOID_START_PLATFORM);
            cf(c, "desert_well", Feature.DESERT_WELL);
            cf(c, "ice_spike", Feature.ICE_SPIKE);
            cf(c, "glowstone_blob", Feature.GLOWSTONE_BLOB);
            cf(c, "freeze_top_later", Feature.FREEZE_TOP_LAYER);
            cf(c, "vines", Feature.VINES);
            cf(c, "monster_room", Feature.MONSTER_ROOM);
            cf(c, "blue_ice", Feature.BLUE_ICE);
            cf(c, "end_platform", Feature.END_PLATFORM);
            cf(c, "end_island", Feature.END_ISLAND);
            cf(c, "kelp", Feature.KELP);
            cf(c, "coral_tree", Feature.CORAL_TREE);
            cf(c, "coral_mushroom", Feature.CORAL_MUSHROOM);
            cf(c, "coral_claw", Feature.CORAL_CLAW);
            cf(c, "weeping_vines", Feature.WEEPING_VINES);
            cf(c, "basalt_pillar", Feature.BASALT_PILLAR);
            cf(c, "bonus_chest", Feature.BONUS_CHEST);
            // Tree
            cf(c, "flower", RandomPatchBuilder.class, RandomPatchBuilder.factory(Feature.FLOWER));
            cf(c, "no_bonemeal_flower", RandomPatchBuilder.class, RandomPatchBuilder.factory(Feature.NO_BONEMEAL_FLOWER));
            cf(c, "random_patch", RandomPatchBuilder.class, RandomPatchBuilder.factory(Feature.RANDOM_PATCH));
            // Block pile
            cf(c, "spring", SpringConfigurationBuilder.class, SpringConfigurationBuilder::new);
            // Replace single block
            cf(c, "fossil", FossilConfigurationBuilder.class, FossilConfigurationBuilder::new);
            // TODO: 1.0.0 | All the rest...
        });
        registry.of(NeoForgeRegistries.Keys.BIOME_MODIFIERS, c -> {
            bm(c, "add_features", AddFeaturesBiomeModifierBuilder.class, AddFeaturesBiomeModifierBuilder::new);
            bm(c, "remove_features", RemoveFeaturesBiomeModifierBuilder.class, RemoveFeaturesBiomeModifierBuilder::new);
            bm(c, "add_spawns", AddSpawnsBiomeModifierBuilder.class, AddSpawnsBiomeModifierBuilder::new);
            bm(c, "remove_spawns", RemoveSpawnsBiomeModifierBuilder.class, RemoveSpawnsBiomeModifierBuilder::new);
            bm(c, "add_carvers", AddCarversBiomeModifierBuilder.class, AddCarversBiomeModifierBuilder::new);
            bm(c, "remove_carvers", RemoveCarversBiomeModifierBuilder.class, RemoveCarversBiomeModifierBuilder::new);
            bm(c, "add_spawn_costs", AddSpawnCostsBiomeModifierBuilder.class, AddSpawnCostsBiomeModifierBuilder::new);
            bm(c, "remove_spawn_costs", RemoveSpawnCostsBiomeModifierBuilder.class, RemoveSpawnCostsBiomeModifierBuilder::new);
        });
    }

    private static <C, B extends BuilderBase<? extends C>> void add(BuilderTypeRegistry.Callback<C> callback, ResourceLocation id, Class<B> builderType, BuilderFactory factory) {
        callback.add(id, builderType, factory);
    }

    private static <C extends ConfiguredFeatureBuilder<?, ?>> void cf(BuilderTypeRegistry.Callback<ConfiguredFeature<?, ?>> callback, String name, Class<C> builderType, BuilderFactory factory) {
        add(callback, KubeJS.id(name), builderType, factory);
    }

    private static <F extends Feature<NoneFeatureConfiguration>> void cf(BuilderTypeRegistry.Callback<ConfiguredFeature<?, ?>> callback, String name, F feature) {
        cf(callback, name, ConfiguredFeatureBuilder.NoneConfig.class, ConfiguredFeatureBuilder.NoneConfig.factory(feature));
    }

    private static <M extends BiomeModifier, B extends BiomeModifierBuilder<M>> void bm(BuilderTypeRegistry.Callback<BiomeModifier> callback, String name, Class<B> builderType, Function<ResourceLocation, B> factory) {
        add(callback, KubeJS.id(name), builderType, factory::apply);
    }

    @Override
    public void registerTypeWrappers(TypeWrapperRegistry registry) {
        registry.register(WeightedValue.class, WeightedValue::wrap);
    }

    @Override
    public void registerRecordDefaults(RecordDefaultsRegistry registry) {
        registry.register(new AddSpawnsBiomeModifierBuilder.Spawn(EntityType.ITEM, 1, 0, 1));
    }

    @Override
    public void registerServerRegistries(ServerRegistryRegistry registry) {
        registry.register(NeoForgeRegistries.Keys.BIOME_MODIFIERS, BiomeModifier.DIRECT_CODEC, TypeInfo.of(BiomeModifier.class));
    }
}
