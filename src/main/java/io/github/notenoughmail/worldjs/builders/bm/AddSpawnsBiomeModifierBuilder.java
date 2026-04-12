package io.github.notenoughmail.worldjs.builders.bm;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.BiomeModifierBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.BiomeModifiers;

import java.util.List;

@ReturnsSelf
public class AddSpawnsBiomeModifierBuilder extends BiomeModifierBuilder<BiomeModifiers.AddSpawnsBiomeModifier> {

    public transient List<MobSpawnSettings.SpawnerData> spawns;

    public AddSpawnsBiomeModifierBuilder(ResourceLocation id) {
        super(id);
        spawns = List.of();
    }

    @Info("The spawns to add to the biome(s)")
    public AddSpawnsBiomeModifierBuilder spawns(List<Spawn> spawns) {
        this.spawns = spawns.stream().map(Spawn::toVanilla).toList();
        return this;
    }

    @Override
    public BiomeModifiers.AddSpawnsBiomeModifier createObject() {
        return new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes,
                spawns
        );
    }

    @Info(params = {
            @Param(name = "entityType", value = "The entity to add a spawn for"),
            @Param(name = "weight", value = "The entity's spawn weight, relative to the sum of all spawn weights"),
            @Param(name = "minCount", value = "When spawning, the minimum number of entities to spawn"),
            @Param(name = "maxCount", value = "When spawning, the maximum number of entities to spawn")
    })
    public record Spawn(EntityType<?> entityType, int weight, int minCount, int maxCount) {

        public MobSpawnSettings.SpawnerData toVanilla() {
            return new MobSpawnSettings.SpawnerData(entityType, weight, minCount, maxCount);
        }
    }
}
