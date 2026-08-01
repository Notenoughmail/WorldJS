package io.github.notenoughmail.worldjs.builders.bs;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.BiomeSourceBuilder;
import io.github.notenoughmail.worldjs.types.assist.ClimateParameterListBuilder;
import io.github.notenoughmail.worldjs.util.Validations;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@Info("Places biomes using noise values")
@ReturnsSelf
public class MultiNoiseBiomeSourceBuilder extends BiomeSourceBuilder<MultiNoiseBiomeSource> {

    @Nullable
    public transient Holder.Reference<MultiNoiseBiomeSourceParameterList> preset;
    @Nullable
    public transient Climate.ParameterList<Holder<Biome>> parameters;

    public MultiNoiseBiomeSourceBuilder(ResourceLocation id) {
        super(id);
    }

    @Info("The parameter preset to use")
    public MultiNoiseBiomeSourceBuilder usingPreset(Holder.Reference<MultiNoiseBiomeSourceParameterList> preset) {
        this.preset = preset;
        return this;
    }

    @Info("Build the climate parameters using a callback")
    public MultiNoiseBiomeSourceBuilder usingParameters(Consumer<ClimateParameterListBuilder> parameters) {
        this.parameters = Util.make(new ClimateParameterListBuilder(), parameters).build();
        return this;
    }

    @Override
    protected MultiNoiseBiomeSource create() {
        if (preset != null) {
            return MultiNoiseBiomeSource.createFromPreset(preset);
        } else if (parameters != null) {
            return MultiNoiseBiomeSource.createFromList(parameters);
        } else {
            throw Validations.exception(sourceLine, "'preset' or 'parameters' must be set!");
        }
    }
}
