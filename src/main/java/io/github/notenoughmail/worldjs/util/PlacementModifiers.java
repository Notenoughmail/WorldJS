package io.github.notenoughmail.worldjs.util;

import dev.latvian.mods.kubejs.util.Lazy;
import dev.latvian.mods.rhino.util.HideFromJS;
import io.github.notenoughmail.worldjs.util.event.PlacedFeatureModifierEvent;
import io.github.notenoughmail.worldjs.util.synmethod.MethodNamespace;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

public enum PlacementModifiers implements NamespacedScriptable<PlacementModifier> {
    INSTANCE;

    private static Consumer<PlacementModifier> modifierRet;

    @HideFromJS
    public static final Lazy<Map<String, MethodNamespace<PlacementModifier>>> NAMESPACES = Lazy.of(PlacedFeatureModifierEvent::createNamespaces);

    @HideFromJS
    public static void accept(Consumer<PlacementModifier> ret, Consumer<PlacementModifiers> source) {
        modifierRet = ret;
        source.accept(INSTANCE);
        modifierRet = null;
    }

    @HideFromJS
    public static void accept(PlacementModifier mod) {
        if (modifierRet != null) modifierRet.accept(mod);
    }

    @Override
    public Map<String, MethodNamespace<PlacementModifier>> namespaces() {
        return NAMESPACES.get();
    }

    @Override
    public String getClassName() {
        return toString();
    }


    @Override
    public String toString() {
        return "PlacementModifiers";
    }
}
