package io.github.notenoughmail.worldjs.types.assist;

import com.mojang.datafixers.util.Pair;
import dev.latvian.mods.kubejs.typings.Info;
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

    @Info("Add and subsequently modify a climate parameter entry for the given biome")
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

        @Info("The temperature to place the biome at")
        public Entry temperature(float value) { return temperature(value, value); }
        @Info("The temperature range to place the biome in")
        public Entry temperature(float min, float max) {
            point.temperature = param(min, max, "temperature");
            return this;
        }
        @Info("The humidity to place the biome at")
        public Entry humidity(float value) { return humidity(value, value); }
        @Info("The humidity range to place the biome in")
        public Entry humidity(float min, float max) {
            point.humidity = param(min, max, "humidity");
            return this;
        }
        @Info("The continentalness to place the biome at")
        public Entry continentalness(float value) { return continentalness(value, value); }
        @Info("The continentalness range to palce the biome in")
        public Entry continentalness(float min, float max) {
            point.continentalness = param(min, max, "continentalness");
            return this;
        }
        @Info("The erosion to place the biome at")
        public Entry erosion(float value) { return erosion(value, value); }
        @Info("The erosion range to place the biome in")
        public Entry erosion(float min, float max) {
            point.erosion = param(min, max, "erosion");
            return this;
        }
        @Info("The depth to place the biome at")
        public Entry depth(float value) { return depth(value, value); }
        @Info("The depth range to place the biome in")
        public Entry depth(float min, float max) {
            point.depth = param(min, max, "depth");
            return this;
        }
        @Info("The weirdness to place the biome at")
        public Entry weirdness(float value) { return weirdness(value, value); }
        @Info("The weirdness range to place the biome in")
        public Entry weirdness(float min, float max) {
            point.weirdness = param(min, max, "weirdness");
            return this;
        }
        @Info("The suitability offset")
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
