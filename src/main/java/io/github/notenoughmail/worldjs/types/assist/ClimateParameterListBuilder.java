package io.github.notenoughmail.worldjs.types.assist;

import com.mojang.datafixers.util.Pair;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.util.Validations;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

import java.util.ArrayList;
import java.util.List;

public class ClimateParameterListBuilder {

    private final List<Entry> entries = new ArrayList<>();

    public Entry forBiome(Holder.Reference<Biome> biome) {
        final Entry entry = new Entry(biome, new MutableParamPoint());
        entries.add(entry);
        return entry;
    }

    @HideFromJS
    public Climate.ParameterList<Holder<Biome>> build() {
        return new Climate.ParameterList<>(
                entries.stream()
                        .map(entry -> Pair.of(
                                entry.point().toPoint(),
                                entry.biome()
                        ))
                        .toList()
        );
    }

    @ReturnsSelf
    public record Entry(
            @HideFromJS
            Holder<Biome> biome,
            @HideFromJS
            MutableParamPoint point
    ) {

        private static Climate.Parameter param(float min, float max, String name) {
            if (min > max)
                throw new IllegalArgumentException("'" + name + "' interval has min > max");
            return new Climate.Parameter(
                    Climate.quantizeCoord(Validations.assertRange(min, -2F, 2F, name + ".min")),
                    Climate.quantizeCoord(Validations.assertRange(max, -2F, 2F, name + ".max"))
            );
        }

        public Entry temperature(float value) { return temperature(value, value); }
        public Entry temperature(float min, float max) {
            point.temperature = param(min, max, "temperature");
            return this;
        }
        public Entry humidity(float value) { return humidity(value, value); }
        public Entry humidity(float min, float max) {
            point.humidity = param(min, max, "humidity");
            return this;
        }
        public Entry continentalness(float value) { return continentalness(value, value); }
        public Entry continentalness(float min, float max) {
            point.continentalness = param(min, max, "continentalness");
            return this;
        }
        public Entry erosion(float value) { return erosion(value, value); }
        public Entry erosion(float min, float max) {
            point.erosion = param(min, max, "erosion");
            return this;
        }
        public Entry depth(float value) { return depth(value, value); }
        public Entry depth(float min, float max) {
            point.depth = param(min, max, "depth");
            return this;
        }
        public Entry weirdness(float value) { return weirdness(value, value); }
        public Entry weirdness(float min, float max) {
            point.weirdness = param(min, max, "weirdness");
            return this;
        }
        public Entry offset(float offset) {
            point.offset = Climate.quantizeCoord(Validations.assertUnit(offset, "offset"));
            return this;
        }
    }

    @HideFromJS
    static final class MutableParamPoint {

        static final Climate.Parameter EMPTY = new Climate.Parameter(0L, 0L);
        Climate.Parameter
                temperature = EMPTY,
                humidity = EMPTY,
                continentalness = EMPTY,
                erosion = EMPTY,
                depth = EMPTY,
                weirdness = EMPTY;
        long offset = 0L;

        Climate.ParameterPoint toPoint() {
            return new Climate.ParameterPoint(
                    temperature,
                    humidity,
                    continentalness,
                    erosion,
                    depth,
                    weirdness,
                    offset
            );
        }
    }
}
