package io.github.notenoughmail.worldjs.builders.base;

import dev.latvian.mods.kubejs.color.KubeColor;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ColorWrapper;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.util.ServerRegistryHolderSet;
import io.github.notenoughmail.worldjs.util.Validations;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeSpecialEffectsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@ReturnsSelf
public class BiomeBuilder extends BuilderBase<Biome> {

    public transient final BiomeSpecialEffectsBuilder specialEffects = BiomeSpecialEffectsBuilder.create(
            ColorWrapper.NONE.kjs$getRGB(),
            ColorWrapper.NONE.kjs$getRGB(),
            ColorWrapper.NONE.kjs$getRGB(),
            ColorWrapper.NONE.kjs$getRGB()
    );

    public transient final MobSpawnSettings.Builder mobSpawns = new MobSpawnSettings.Builder();

    public transient boolean precip = true;
    public transient float temperature, downfall;
    public transient Biome.TemperatureModifier tempMod = Biome.TemperatureModifier.NONE;
    public transient Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>> carvers = Map.of();
    public transient HolderSet<PlacedFeature>[] features = new HolderSet[GenerationStep.Decoration.values().length];

    public BiomeBuilder(ResourceLocation id) {
        super(id);
        Arrays.fill(features, HolderSet.empty());
    }

    public BiomeBuilder temperature(float temp) {
        temperature = temp;
        return this;
    }

    public BiomeBuilder downfall(float downfall) {
        this.downfall = downfall;
        return this;
    }

    public BiomeBuilder fogColor(KubeColor color) {
        specialEffects.fogColor(color.kjs$getRGB());
        return this;
    }

    public BiomeBuilder waterColor(KubeColor color) {
        specialEffects.waterColor(color.kjs$getRGB());
        return this;
    }

    public BiomeBuilder waterFogColor(KubeColor color) {
        specialEffects.waterFogColor(color.kjs$getRGB());
        return this;
    }

    public BiomeBuilder skyColor(KubeColor color) {
        specialEffects.skyColor(color.kjs$getRGB());
        return this;
    }

    public BiomeBuilder foliageColor(KubeColor color) {
        specialEffects.foliageColorOverride(color.kjs$getRGB());
        return this;
    }

    public BiomeBuilder grassColor(KubeColor color) {
        specialEffects.grassColorOverride(color.kjs$getRGB());
        return this;
    }

    public BiomeBuilder grassColorModifier(BiomeSpecialEffects.GrassColorModifier modifier) {
        specialEffects.grassColorModifier(modifier);
        return this;
    }

    public BiomeBuilder particle(ParticleOptions particleOptions, float probability) {
        specialEffects.ambientParticle(new AmbientParticleSettings(particleOptions, probability));
        return this;
    }

    public BiomeBuilder ambientSound(Holder.Reference<SoundEvent> sound) {
        specialEffects.ambientLoopSound(sound);
        return this;
    }

    public BiomeBuilder moodSound(
            Holder.Reference<SoundEvent> sound,
            int tickDelay,
            int blockSearchExtent,
            double offset
    ) {
        specialEffects.ambientMoodSound(new AmbientMoodSettings(sound, tickDelay, blockSearchExtent, offset));
        return this;
    }

    public BiomeBuilder additionsSound(Holder.Reference<SoundEvent> sound, double tickChance) {
        specialEffects.ambientAdditionsSound(new AmbientAdditionsSettings(sound, tickChance));
        return this;
    }

    public BiomeBuilder music(Holder.Reference<SoundEvent> sound, int minDelay, int maxDelay, boolean replaceCurrentMusic) {
        specialEffects.backgroundMusic(new Music(sound, minDelay, maxDelay, replaceCurrentMusic));
        return this;
    }

    public BiomeBuilder temperatureModifier(Biome.TemperatureModifier modifier) {
        tempMod = modifier;
        return this;
    }

    public BiomeBuilder withoutPrecipitation() {
        precip = false;
        return this;
    }

    public BiomeBuilder carving(GenerationStep.Carving carvingStep, ServerRegistryHolderSet<ConfiguredWorldCarver<?>> carver) {
        carvers.put(carvingStep, carver.convertWithValidation("carving.carver", sourceLine));
        return this;
    }

    public BiomeBuilder features(GenerationStep.Decoration step, ServerRegistryHolderSet<PlacedFeature> feature) {
        features[step.ordinal()] = feature.convertWithValidation("features.feature", sourceLine);
        return this;
    }

    public BiomeBuilder addSpawn(MobCategory category, EntityType<?> type, int weight, int minCount, int maxCount) {
        mobSpawns.addSpawn(category, new MobSpawnSettings.SpawnerData(
                type,
                Validations.assertPositive(weight, "addSpawn.weight"),
                Validations.assertNotGreaterThan(
                        Validations.assertPositive(minCount, "addSpawn.minCount"),
                        Validations.assertPositive(maxCount, "addSpawn.maxCount"),
                        "addSpawn.minCount",
                        "addSpawn.maxCount",
                        sourceLine
                ),
                maxCount
        ));
        return this;
    }

    public BiomeBuilder addMobCharge(EntityType<?> type, double charge, double energyBudget) {
        mobSpawns.addMobCharge(type, charge, energyBudget);
        return this;
    }

    public BiomeBuilder creatureGenerationProbability(float probability) {
        mobSpawns.creatureGenerationProbability(Validations.assertRange(probability, 0F, 0.9999999F, "creatureGenerationProbability"));
        return this;
    }

    @Override
    public Biome createObject() {
        return new Biome(
                new Biome.ClimateSettings(
                        precip,
                        temperature,
                        tempMod,
                        downfall
                ),
                // Colors are RGB24
                specialEffects.build(),
                new BiomeGenerationSettings(
                        carvers,
                        List.of(features)
                ),
                mobSpawns.build()
        );
    }
}
