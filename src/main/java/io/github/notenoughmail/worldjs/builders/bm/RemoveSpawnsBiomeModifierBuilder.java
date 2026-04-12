package io.github.notenoughmail.worldjs.builders.bm;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.builders.base.BiomeModifierBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.world.BiomeModifiers;

@ReturnsSelf
public class RemoveSpawnsBiomeModifierBuilder extends BiomeModifierBuilder<BiomeModifiers.RemoveSpawnsBiomeModifier> {

    public transient HolderSet<EntityType<?>> entityTypes;

    public RemoveSpawnsBiomeModifierBuilder(ResourceLocation id) {
        super(id);
        entityTypes = HolderSet.empty();
    }

    @Info("The entity(s) to remove spawn for")
    public RemoveSpawnsBiomeModifierBuilder entityTypes(HolderSet<EntityType<?>> entityTypes) {
        this.entityTypes = entityTypes;
        return this;
    }

    @Override
    public BiomeModifiers.RemoveSpawnsBiomeModifier createObject() {
        return new BiomeModifiers.RemoveSpawnsBiomeModifier(
                biomes,
                entityTypes
        );
    }
}
