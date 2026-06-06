package io.github.notenoughmail.worldjs.util.mixin;

import dev.latvian.mods.kubejs.registry.BuilderType;
import dev.latvian.mods.kubejs.registry.BuilderTypeRegistryHandler;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(BuilderTypeRegistryHandler.Info.class)
public interface InfoAccessor<T> {

    @Accessor("types")
    Map<ResourceLocation, BuilderType<T>> worldjs$GetTypes();
}
