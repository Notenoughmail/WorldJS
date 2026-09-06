package io.github.notenoughmail.worldjs.util.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.latvian.mods.kubejs.util.Cast;
import io.github.notenoughmail.worldjs.builders.base.WorldPresetBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.OptionalLong;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin {

    @Shadow
    @Final
    WorldCreationUiState uiState;

    @WrapOperation(method = "openFresh", at = @At(value = "INVOKE", target = "Ljava/util/Optional;of(Ljava/lang/Object;)Ljava/util/Optional;", ordinal = 0))
    private static <T> Optional<T> worldjs$UseScriptDefault(T original, Operation<Optional<T>> operation) {
        final T value = WorldPresetBuilder.USE_AS_DEFAULT == null ? original : Cast.to(WorldPresetBuilder.USE_AS_DEFAULT);
        return operation.call(value);
    }

    // Force kube preset when it doesn't have the same dimensions as the regular preset
    @Inject(method = "<init>", at = @At("RETURN"))
    private void worldjs$FixPreset(
            Minecraft minecraft,
            Screen lastScreen,
            WorldCreationContext settings,
            Optional<ResourceKey<WorldPreset>> preset,
            OptionalLong seed,
            CallbackInfo ci
    ) {
        if (preset.orElse(null) == WorldPresetBuilder.USE_AS_DEFAULT) {
            uiState.setWorldType(uiState.getWorldType());
        }
    }
}
