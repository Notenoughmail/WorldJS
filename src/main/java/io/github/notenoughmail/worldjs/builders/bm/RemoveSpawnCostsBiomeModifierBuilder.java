package io.github.notenoughmail.worldjs.builders.bm;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.BiomeModifierBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.world.BiomeModifiers;

@ReturnsSelf
public class RemoveSpawnCostsBiomeModifierBuilder extends BiomeModifierBuilder<BiomeModifiers.RemoveSpawnCostsBiomeModifier> {

    public transient HolderSet<EntityType<?>> entityTypes;

    public RemoveSpawnCostsBiomeModifierBuilder(ResourceLocation id) {
        super(id);
        entityTypes = HolderSet.empty();
    }

    @Info("The entity(s) to remove spawn costs for")
    public RemoveSpawnCostsBiomeModifierBuilder entityTypes(HolderSet<EntityType<?>> entityTypes) {
        this.entityTypes = entityTypes;
        return this;
    }

    @Override
    public BiomeModifiers.RemoveSpawnCostsBiomeModifier createObject() {
        return new BiomeModifiers.RemoveSpawnCostsBiomeModifier(
                biomes,
                entityTypes
        );
    }
}
