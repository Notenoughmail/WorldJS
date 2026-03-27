package io.github.notenoughmail.worldjs.builders.bm;

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

    public record Spawn(EntityType<?> entityType, int weight, int minCount, int maxCount) {

        public MobSpawnSettings.SpawnerData toVanilla() {
            return new MobSpawnSettings.SpawnerData(entityType, weight, minCount, maxCount);
        }
    }
}
