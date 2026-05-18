package io.github.notenoughmail.worldjs.builders.bm;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.world.NoneBiomeModifier;

public class NoneBiomeModifierBuilder extends BuilderBase<NoneBiomeModifier> {

    public NoneBiomeModifierBuilder(ResourceLocation id) {
        super(id);
    }

    @Override
    public NoneBiomeModifier createObject() {
        return NoneBiomeModifier.INSTANCE;
    }
}
