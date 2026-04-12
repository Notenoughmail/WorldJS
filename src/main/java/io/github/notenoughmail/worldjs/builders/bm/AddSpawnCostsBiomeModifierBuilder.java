package io.github.notenoughmail.worldjs.builders.bm;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.BiomeModifierBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.BiomeModifiers;

@ReturnsSelf
public class AddSpawnCostsBiomeModifierBuilder extends BiomeModifierBuilder<BiomeModifiers.AddSpawnCostsBiomeModifier> {

    public transient HolderSet<EntityType<?>> entityTypes;
    public transient MobSpawnSettings.MobSpawnCost cost;

    public AddSpawnCostsBiomeModifierBuilder(ResourceLocation id) {
        super(id);
        entityTypes = HolderSet.empty();
        cost = new MobSpawnSettings.MobSpawnCost(1, 0.5);
    }

    @Info("The entity(s) to add a spawn cost for")
    public AddSpawnCostsBiomeModifierBuilder entityTypes(HolderSet<EntityType<?>> entityTypes) {
        this.entityTypes = entityTypes;
        return this;
    }

    @Info("The spawn budget and cost for the entities")
    public AddSpawnCostsBiomeModifierBuilder cost(double spawnBudget, double costPerSpawn) {
        cost = new MobSpawnSettings.MobSpawnCost(spawnBudget, costPerSpawn);
        return this;
    }

    @Override
    public BiomeModifiers.AddSpawnCostsBiomeModifier createObject() {
        return new BiomeModifiers.AddSpawnCostsBiomeModifier(
                biomes,
                entityTypes,
                cost
        );
    }
}
