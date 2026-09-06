package io.github.notenoughmail.worldjs.builders.base;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.HideFromJS;
import io.github.notenoughmail.worldjs.WorldJS;
import io.github.notenoughmail.worldjs.util.Validations;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.WorldPresetTags;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class WorldPresetBuilder extends BuilderBase<WorldPreset> {

    @HideFromJS
    public static ResourceKey<WorldPreset> USE_AS_DEFAULT;

    public transient final Map<ResourceKey<LevelStem>, LevelStem> stems = new IdentityHashMap<>();

    public WorldPresetBuilder(ResourceLocation id) {
        super(id);
    }

    @Info(value = "Add a dimension to the preset. 'minecraft:overworld' must be defined", params = {
            @Param(name = "id", value = "The id of the dimension"),
            @Param(name = "dimensionType", value = "The id of a pre-existing dimension type to use for the dimension"),
            @Param(name = "generatorType", value = "The chunk generator type to use for the dimension"),
            @Param(name = "generatorBuilder", value = "Builder for the chunk generator")
    })
    public WorldPresetBuilder withDimension(
            Context ctx,
            ResourceKey<LevelStem> id,
            Holder.Reference<DimensionType> dimensionType,
            ResourceLocation generatorType,
            Consumer<ChunkGeneratorBuilder<?>> generatorBuilder
    ) {
        final ChunkGenerator generator = WorldJS.CHUNK_GENERATOR_TYPES.build(ctx, generatorType, id, generatorBuilder);
        stems.put(id, new LevelStem(dimensionType, generator));
        return this;
    }

    @Info("Adds the preset to the 'minecraft:normal' tag to be available in the create world screen")
    public WorldPresetBuilder addToNormalPresetList() {
        defaultTags.add(WorldPresetTags.NORMAL.location());
        return this;
    }

    @Info("Adds the preset to the 'minecraft:extended' tag to be available in the create world screen as a hidden option")
    public WorldPresetBuilder addToHiddenPresetList() {
        defaultTags.add(WorldPresetTags.EXTENDED.location());
        return this;
    }

    @Info("Makes this preset be the preset selected by default on the create world screen")
    public WorldPresetBuilder asDefaultPreset() {
        USE_AS_DEFAULT = ResourceKey.create(Registries.WORLD_PRESET, id);
        return this;
    }

    @Override
    public WorldPreset createObject() {
        return new WorldPreset(Validations.validate(
                stems,
                m -> m.containsKey(LevelStem.OVERWORLD) ? null : "'minecraft:overworld' must be defined!",
                sourceLine
        ));
    }
}
