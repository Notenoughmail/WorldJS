package io.github.notenoughmail.worldjs.util;

import dev.latvian.mods.rhino.type.TypeInfo;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Consumer;

public interface Types {

    TypeInfo BLOCK_STATE_PROVIDER = TypeInfo.of(BlockStateProvider.class);
    TypeInfo WEIGHTED_BLOCK_STATE_LIST = WeightedValue.listType(BlockState.class);
    TypeInfo BLOCK = TypeInfo.of(Block.class);
    TypeInfo BLOCK_STATE = TypeInfo.of(BlockState.class);
    TypeInfo INT_PROVIDER = TypeInfo.of(IntProvider.class);
    TypeInfo VERTICAL_ANCHOR = TypeInfo.of(VerticalAnchor.class);
    TypeInfo WEIGHTED_HEIGHT_PROVIDER_LIST = WeightedValue.listType(HeightProvider.class);
    TypeInfo BLOCK_PREDICATE = TypeInfo.of(BlockPredicate.class);
    TypeInfo LIST_BLOCK_PREDICATE = list(BLOCK_PREDICATE);
    TypeInfo VEC3I = TypeInfo.of(Vec3i.class);
    TypeInfo HOLDER_SET = TypeInfo.of(HolderSet.class);
    TypeInfo REF_HOLDER = TypeInfo.of(Holder.Reference.class);
    TypeInfo BLOCK_HOLDER_SET = holderSet(BLOCK);
    TypeInfo FLUID_HOLDER_SET = holderSet(TypeInfo.of(Fluid.class));
    TypeInfo BLOCK_TAG = TypeInfo.of(TagKey.class).withParams(BLOCK);
    TypeInfo CONDITION_SOURCE = TypeInfo.of(SurfaceRules.ConditionSource.class);
    TypeInfo RESOURCE_KEY = TypeInfo.of(ResourceKey.class);
    TypeInfo BIOME_RES_KEY = resourceKey(Biome.class);
    TypeInfo NOISE_PARAMS = TypeInfo.of(NormalNoise.NoiseParameters.class);
    TypeInfo NOISE_PARAMS_RES_KEY = RESOURCE_KEY.withParams(NOISE_PARAMS);
    TypeInfo DENSITY_FUNCTION = TypeInfo.of(DensityFunction.class);
    TypeInfo FLOAT_2_FLOAT = TypeInfo.of(Float2FloatFunction.class);

    TypeInfo DIRECTION = TypeInfo.of(Direction.class);
    TypeInfo HEIGHTMAP = TypeInfo.of(Heightmap.Types.class);

    TypeInfo INT = TypeInfo.PRIMITIVE_INT;
    TypeInfo BOOL = TypeInfo.PRIMITIVE_BOOLEAN;
    TypeInfo DOUB = TypeInfo.PRIMITIVE_DOUBLE;
    TypeInfo STR = TypeInfo.STRING;

    TypeInfo PARSE_MAP = TypeInfo.RAW_MAP.withParams(STR, TypeInfo.OBJECT);
    TypeInfo CONSUMER = TypeInfo.of(Consumer.class);

    static TypeInfo list(TypeInfo param) {
        return TypeInfo.RAW_LIST.withParams(param);
    }

    static TypeInfo holderSet(TypeInfo param) {
        return HOLDER_SET.withParams(param);
    }

    static TypeInfo resourceKey(Class<?> type) {
        return RESOURCE_KEY.withParams(TypeInfo.of(type));
    }

    static TypeInfo refHolder(Class<?> type) {
        return REF_HOLDER.withParams(TypeInfo.of(type));
    }

    static TypeInfo consumer(TypeInfo type) {
        return CONSUMER.withParams(type);
    }
}
