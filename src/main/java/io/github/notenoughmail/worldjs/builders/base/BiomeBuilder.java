package io.github.notenoughmail.worldjs.builders.base;

import dev.latvian.mods.kubejs.color.KubeColor;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ColorWrapper;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
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
import java.util.EnumMap;
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
    public transient Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>> carvers = new EnumMap<>(GenerationStep.Carving.class);
    public transient HolderSet<PlacedFeature>[] features = new HolderSet[GenerationStep.Decoration.values().length];

    public BiomeBuilder(ResourceLocation id) {
        super(id);
        Arrays.fill(features, HolderSet.empty());
    }

    @Info("Controls the grass/foliage color and height-adjusted temperature")
    public BiomeBuilder temperature(float temp) {
        temperature = temp;
        return this;
    }

    @Info("Controls the grass/foliage color")
    public BiomeBuilder downfall(float downfall) {
        this.downfall = downfall;
        return this;
    }

    @Info("The color used for fog")
    public BiomeBuilder fogColor(KubeColor color) {
        specialEffects.fogColor(color.kjs$getRGB());
        return this;
    }

    @Info("The color used for water")
    public BiomeBuilder waterColor(KubeColor color) {
        specialEffects.waterColor(color.kjs$getRGB());
        return this;
    }

    @Info("The color used for water fog")
    public BiomeBuilder waterFogColor(KubeColor color) {
        specialEffects.waterFogColor(color.kjs$getRGB());
        return this;
    }

    @Info("The color used for the sky")
    public BiomeBuilder skyColor(KubeColor color) {
        specialEffects.skyColor(color.kjs$getRGB());
        return this;
    }

    @Info("The color used for leaves and vines")
    public BiomeBuilder foliageColor(KubeColor color) {
        specialEffects.foliageColorOverride(color.kjs$getRGB());
        return this;
    }

    @Info("The color used for grass, ferns, and sugarcane")
    public BiomeBuilder grassColor(KubeColor color) {
        specialEffects.grassColorOverride(color.kjs$getRGB());
        return this;
    }

    @Info("A modifier to apply to the grass color")
    public BiomeBuilder grassColorModifier(BiomeSpecialEffects.GrassColorModifier modifier) {
        specialEffects.grassColorModifier(modifier);
        return this;
    }

    @Info(value = "The ambient particle of the biome", params = {
            @Param(name = "particleOptions", value = "The particle to spawn"),
            @Param(name = "probability", value = "How often the particle spawns")
    })
    public BiomeBuilder particle(ParticleOptions particleOptions, float probability) {
        specialEffects.ambientParticle(new AmbientParticleSettings(particleOptions, probability));
        return this;
    }

    @Info("The ambient sound")
    public BiomeBuilder ambientSound(Holder.Reference<SoundEvent> sound) {
        specialEffects.ambientLoopSound(sound);
        return this;
    }

    @Info(value = "The mood properties of the biome", params = {
            @Param(name = "sound", value = "The mood sound"),
            @Param(name = "tickDelay", value = "The minimum delay between two plays"),
            @Param(name = "blockSearchExtent", value = "The range at which the mood algorithm can check"),
            @Param(name = "offset", value = "How far the sound source should be from the player")
    })
    public BiomeBuilder moodSound(
            Holder.Reference<SoundEvent> sound,
            int tickDelay,
            int blockSearchExtent,
            double offset
    ) {
        specialEffects.ambientMoodSound(new AmbientMoodSettings(sound, tickDelay, blockSearchExtent, offset));
        return this;
    }

    @Info(value = "Settings for additions sound", params = {
            @Param(name = "sound", value = "The sound to play"),
            @Param(name = "tickChance", value = "The probability of playing the sound each tick")
    })
    public BiomeBuilder additionsSound(Holder.Reference<SoundEvent> sound, double tickChance) {
        specialEffects.ambientAdditionsSound(new AmbientAdditionsSettings(sound, tickChance));
        return this;
    }

    @Info(value = "Specific music that should play in the biome", params = {
            @Param(name = "sound", value = "The music sound"),
            @Param(name = "minDelay", value = "The minimum delay between two music tracks"),
            @Param(name = "maxDelay", value = "The maximum delay between two music tracks"),
            @Param(name = "replaceCurrentMusic", value = "If currently playing music should be replaced")
    })
    public BiomeBuilder music(Holder.Reference<SoundEvent> sound, int minDelay, int maxDelay, boolean replaceCurrentMusic) {
        specialEffects.backgroundMusic(new Music(sound, minDelay, maxDelay, replaceCurrentMusic));
        return this;
    }

    @Info("Modifies the temperature before calculating the height adjusted temperature")
    public BiomeBuilder temperatureModifier(Biome.TemperatureModifier modifier) {
        tempMod = modifier;
        return this;
    }

    @Info("Disables precipitation in the biome")
    public BiomeBuilder withoutPrecipitation() {
        precip = false;
        return this;
    }

    @Info(value = "The world carvers to use in the given caring step", params = {
            @Param(name = "caringStep", value = "The carving step"),
            @Param(name = "carver", value = "The carvers that will carve during the given carving step")
    })
    public BiomeBuilder carving(GenerationStep.Carving carvingStep, ServerRegistryHolderSet<ConfiguredWorldCarver<?>> carver) {
        carvers.put(carvingStep, carver.convertWithValidation("carving.carver", sourceLine));
        return this;
    }

    @Info(value = "The placed features to place in the given decoration step", params = {
            @Param(name = "step", value = "The decoration step"),
            @Param(name = "feature", value = "The features")
    })
    public BiomeBuilder features(GenerationStep.Decoration step, ServerRegistryHolderSet<PlacedFeature> feature) {
        features[step.ordinal()] = feature.convertWithValidation("features.feature", sourceLine);
        return this;
    }

    @Info(value = "Add a mob spawn", params = {
            @Param(name = "category", value = "The category of the spawn"),
            @Param(name = "type", value = "The entity type to spawn"),
            @Param(name = "weight", value = "How often the mob should spawn"),
            @Param(name = "minCount", value = "The minimum number of the entity to spawn"),
            @Param(name = "maxCount", value = "The maximum number of the entity to spawn")
    })
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

    @Info(value = "Add a spawn cost for an entity", params = {
            @Param(name = "type", value = "The entity type to add a cost for"),
            @Param(name = "charge", value = "The charge of the mob"),
            @Param(name = "energyBudget", value = "The mob's maximum potential")
    })
    public BiomeBuilder addMobCharge(EntityType<?> type, double charge, double energyBudget) {
        mobSpawns.addMobCharge(type, charge, energyBudget);
        return this;
    }

    @Info("The probability of creatures being spawned during world generation")
    public BiomeBuilder creatureSpawnProbability(float probability) {
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
