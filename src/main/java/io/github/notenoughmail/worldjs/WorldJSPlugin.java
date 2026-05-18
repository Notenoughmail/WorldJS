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
import io.github.notenoughmail.worldjs.builders.base.ConfiguredFeatureBuilder;
import io.github.notenoughmail.worldjs.builders.base.PlacedFeatureBuilder;
import io.github.notenoughmail.worldjs.builders.bm.*;
import io.github.notenoughmail.worldjs.builders.cf.*;
import io.github.notenoughmail.worldjs.util.WeightedValue;
import io.github.notenoughmail.worldjs.util.Wrappers;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

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
            cf(c, "tree", TreeConfigurationBuilder.class, TreeConfigurationBuilder::new);
            cf(c, "flower", RandomPatchConfigurationBuilder.class, Feature.FLOWER, RandomPatchConfigurationBuilder::new);
            cf(c, "no_bonemeal_flower", RandomPatchConfigurationBuilder.class, Feature.NO_BONEMEAL_FLOWER, RandomPatchConfigurationBuilder::new);
            cf(c, "random_patch", RandomPatchConfigurationBuilder.class, Feature.RANDOM_PATCH, RandomPatchConfigurationBuilder::new);
            cf(c, "block_pile", BlockPileConfigurationBuilder.class, BlockPileConfigurationBuilder::new);
            cf(c, "spring", SpringConfigurationBuilder.class, SpringConfigurationBuilder::new);
            cf(c, "replace_single_block", ReplaceBlockConfigurationBuilder.class, ReplaceBlockConfigurationBuilder::new);
            cf(c, "fossil", FossilConfigurationBuilder.class, FossilConfigurationBuilder::new);
            cf(c, "huge_red_mushroom", HugeMushroomConfigurationBuilder.class, Feature.HUGE_RED_MUSHROOM, HugeMushroomConfigurationBuilder::new);
            cf(c, "huge_brown_mushroom", HugeMushroomConfigurationBuilder.class, Feature.HUGE_BROWN_MUSHROOM, HugeMushroomConfigurationBuilder::new);
            cf(c, "block_column", BlockColumnConfigurationBuilder.class, BlockColumnConfigurationBuilder::new);
            cf(c, "vegetation_patch", VegetationPatchConfigurationBuilder.class, Feature.VEGETATION_PATCH, VegetationPatchConfigurationBuilder::new);
            cf(c, "waterlogged_vegetation_patch", VegetationPatchConfigurationBuilder.class, Feature.WATERLOGGED_VEGETATION_PATCH, VegetationPatchConfigurationBuilder::new);
            // Doc cutoff
            cf(c, "root_system", RootSystemConfigurationBuilder.class, RootSystemConfigurationBuilder::new);
            cf(c, "multiface_growth", MultifaceGrowthConfigurationBuilder.class, MultifaceGrowthConfigurationBuilder::new);
            cf(c, "underwater_magma", UnderwaterMagmaConfigurationBuilder.class, UnderwaterMagmaConfigurationBuilder::new);
            cf(c, "iceberg", BlockStateConfigurationBuilder.class, Feature.ICEBERG, BlockStateConfigurationBuilder::new);
            cf(c, "forest_rock", BlockStateConfigurationBuilder.class, Feature.FOREST_ROCK, BlockStateConfigurationBuilder::new);
            cf(c, "disk", DiskConfigurationBuilder.class, DiskConfigurationBuilder::new);
            cf(c, "lake", LakeConfigurationBuilder.class, LakeConfigurationBuilder::new);
            cf(c, "ore", OreConfigurationBuilder.class, Feature.ORE, OreConfigurationBuilder::new);
            cf(c, "end_spike", SpikeConfigurationBuilder.class, SpikeConfigurationBuilder::new);
            cf(c, "end_gateway", EndGatewayConfigurationBuilder.class, EndGatewayConfigurationBuilder::new);
            cf(c, "seagrass", ProbabilityFeatureConfigurationBuilder.class, Feature.SEAGRASS, ProbabilityFeatureConfigurationBuilder::new);
            cf(c, "sea_pickle", CountConfigurationBuilder.class, CountConfigurationBuilder::new);
            cf(c, "simple_block", SimpleBlockConfigurationBuilder.class, SimpleBlockConfigurationBuilder::new);
            cf(c, "bamboo", ProbabilityFeatureConfigurationBuilder.class, Feature.BAMBOO, ProbabilityFeatureConfigurationBuilder::new);
            cf(c, "huge_fungus", HugeFungusConfigurationBuilder.class, HugeFungusConfigurationBuilder::new);
            cf(c, "nether_forest_vegetation", NetherForestVegetationConfigBuilder.class, NetherForestVegetationConfigBuilder::new);
            cf(c, "twisting_vines", TwistingVinesConfigBuilder.class, TwistingVinesConfigBuilder::new);
            cf(c, "basalt_columns", ColumnFeatureConfigurationBuilder.class, ColumnFeatureConfigurationBuilder::new);
            cf(c, "delta_feature", DeltaFeatureConfigurationBuilder.class, DeltaFeatureConfigurationBuilder::new);
            cf(c, "replace_blobs", ReplaceSphereConfigurationBuilder.class, ReplaceSphereConfigurationBuilder::new); // Registered as "netherrack_replace_blobs", but doesn't appear to be nether(rack) limited
            cf(c, "fill_layer", LayerConfigurationBuilder.class, LayerConfigurationBuilder::new);
            cf(c, "scattered_ore", OreConfigurationBuilder.class, Feature.SCATTERED_ORE, OreConfigurationBuilder::new);
            cf(c, "random_selector", RandomFeatureConfigurationBuilder.class, RandomFeatureConfigurationBuilder::new);
            cf(c, "simple_random_selector", SimpleRandomFeatureConfigurationBuilder.class, SimpleRandomFeatureConfigurationBuilder::new);
            cf(c, "random_boolean_selector", RandomBooleanFeatureConfigurationBuilder.class, RandomBooleanFeatureConfigurationBuilder::new);
            cf(c, "geode", GeodeConfigurationBuilder.class, GeodeConfigurationBuilder::new);
            cf(c, "dripstone_cluster", DripstoneClusterConfigurationBuilder.class, DripstoneClusterConfigurationBuilder::new);
            cf(c, "large_dripstone", LargeDripstoneConfigurationBuilder.class, LargeDripstoneConfigurationBuilder::new);
            cf(c, "pointed_dripstone", PointedDripstoneConfigurationBuilder.class, PointedDripstoneConfigurationBuilder::new);
            cf(c, "sculk_patch", SculkPatchConfigurationBuilder.class, SculkPatchConfigurationBuilder::new);
            add(c, WorldJS.identifier("weighted_random_selector"), WeightedRandomSelectorBuilder.class, WeightedRandomSelectorBuilder::new);
        });
        registry.of(NeoForgeRegistries.Keys.BIOME_MODIFIERS, c -> {
            bm(c, "none", NoneBiomeModifierBuilder.class, NoneBiomeModifierBuilder::new);
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

    private static <C extends ConfiguredFeatureBuilder<FC>, FC extends FeatureConfiguration> void cf(BuilderTypeRegistry.Callback<ConfiguredFeature<?, ?>> callback, String name, Class<C> builderType, Feature<FC> feature, BiFunction<ResourceLocation, Supplier<Feature<FC>>, ? extends C> factory) {
        cf(callback, name, builderType, ConfiguredFeatureBuilder.factory(feature, factory));
    }

    private static <C extends ConfiguredFeatureBuilder<?>> void cf(BuilderTypeRegistry.Callback<ConfiguredFeature<?, ?>> callback, String name, Class<C> builderType, BuilderFactory factory) {
        add(callback, KubeJS.id(name), builderType, factory);
    }

    private static <F extends Feature<NoneFeatureConfiguration>> void cf(BuilderTypeRegistry.Callback<ConfiguredFeature<?, ?>> callback, String name, F feature) {
        cf(callback, name, ConfiguredFeatureBuilder.NoneConfig.class, feature, ConfiguredFeatureBuilder.NoneConfig::new);
    }

    private static <M extends BiomeModifier, B extends BuilderBase<M>> void bm(BuilderTypeRegistry.Callback<BiomeModifier> callback, String name, Class<B> builderType, Function<ResourceLocation, B> factory) {
        add(callback, KubeJS.id(name), builderType, factory::apply);
    }

    @Override
    public void registerTypeWrappers(TypeWrapperRegistry registry) {
        registry.register(WeightedValue.class, WeightedValue::wrap);
        registry.register(BlockStateProvider.class, Wrappers::blockStateProvider);
        registry.register(VerticalAnchor.class, Wrappers::verticalAnchor);
        registry.register(HeightProvider.class, Wrappers::heightProvider);
        registry.register(BlockPredicate.class, Wrappers::blockPredicate);
        registry.registerCodec(TrunkPlacer.class, TrunkPlacer.CODEC);
        registry.registerCodec(FoliagePlacer.class, FoliagePlacer.CODEC);
        registry.registerCodec(RootPlacer.class, RootPlacer.CODEC);
        registry.registerCodec(FeatureSize.class, FeatureSize.CODEC);
        registry.registerCodec(TreeDecorator.class, TreeDecorator.CODEC);
        registry.registerAlias(OreConfiguration.TargetBlockState.class, Wrappers.TargetBlockState.class, Wrappers.TargetBlockState::convert);
    }

    @Override
    public void registerRecordDefaults(RecordDefaultsRegistry registry) {
        registry.register(new AddSpawnsBiomeModifierBuilder.Spawn(null, 1, 0, 1));
        registry.register(new GeodeConfigurationBuilder.Blocks(
                BlockStateProvider.simple(Blocks.AIR),
                BlockStateProvider.simple(Blocks.AMETHYST_BLOCK),
                BlockStateProvider.simple(Blocks.BUDDING_AMETHYST),
                BlockStateProvider.simple(Blocks.CALCITE),
                BlockStateProvider.simple(Blocks.SMOOTH_BASALT),
                List.of(
                        Blocks.SMALL_AMETHYST_BUD.defaultBlockState(),
                        Blocks.MEDIUM_AMETHYST_BUD.defaultBlockState(),
                        Blocks.LARGE_AMETHYST_BUD.defaultBlockState(),
                        Blocks.AMETHYST_CLUSTER.defaultBlockState()
                ),
                BlockTags.FEATURES_CANNOT_REPLACE,
                BlockTags.GEODE_INVALID_BLOCKS
        ));
    }

    @Override
    public void registerServerRegistries(ServerRegistryRegistry registry) {
        registry.register(NeoForgeRegistries.Keys.BIOME_MODIFIERS, BiomeModifier.DIRECT_CODEC, TypeInfo.of(BiomeModifier.class));
    }
}
