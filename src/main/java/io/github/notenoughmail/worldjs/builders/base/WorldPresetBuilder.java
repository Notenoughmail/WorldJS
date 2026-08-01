package io.github.notenoughmail.worldjs.builders.base;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.Context;
import io.github.notenoughmail.worldjs.util.Validations;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class WorldPresetBuilder extends BuilderBase<WorldPreset> {

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
        final ChunkGenerator generator = SubBuilder.build(
                ctx,
                generatorType,
                id,
                generatorBuilder,
                ChunkGeneratorBuilder.ALL_TYPES,
                "chunk generator"
        );
        stems.put(id, new LevelStem(dimensionType, generator));
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
