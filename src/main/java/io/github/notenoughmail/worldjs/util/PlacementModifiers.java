package io.github.notenoughmail.worldjs.util;

import dev.latvian.mods.kubejs.util.Lazy;
import io.github.notenoughmail.worldjs.util.event.PlacedFeatureModifierEvent;
import io.github.notenoughmail.worldjs.util.synmethod.MethodNamespace;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

public enum PlacementModifiers implements NamespacedScriptable<PlacementModifier> {
    INSTANCE;

    public static Consumer<PlacementModifier> modifierRet;

    public static final Lazy<Map<String, MethodNamespace<PlacementModifier>>> NAMESPACES = Lazy.of(PlacedFeatureModifierEvent::createNamespaces);

    public static void accept(Consumer<PlacementModifier> ret, Consumer<PlacementModifiers> source) {
        modifierRet = ret;
        source.accept(INSTANCE);
        modifierRet = null;
    }

    public static void accept(PlacementModifier mod) {
        if (modifierRet != null) modifierRet.accept(mod);
    }

    @Nullable
    @Override
    public MethodNamespace<PlacementModifier> getNamespace(String name) {
        return NAMESPACES.get().get(name);
    }
}
