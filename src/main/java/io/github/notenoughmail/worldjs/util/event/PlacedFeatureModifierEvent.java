package io.github.notenoughmail.worldjs.util.event;

import io.github.notenoughmail.worldjs.util.PlacementModifiers;
import io.github.notenoughmail.worldjs.util.synmethod.MethodNamespace;
import io.github.notenoughmail.worldjs.util.synmethod.NamespaceRegistrar;
import io.github.notenoughmail.worldjs.util.synmethod.SyntheticFunctionEvent;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Register namespaced synthetic {@link PlacementModifier} functions to {@link PlacementModifiers} for use in
 * {@link io.github.notenoughmail.worldjs.builders.base.PlacedFeatureBuilder#modifiers(Consumer) PlacedFeatureBuilder#modifiers}
 */
public final class PlacedFeatureModifierEvent extends Event implements SyntheticFunctionEvent<PlacementModifier> {

    public static Map<String, MethodNamespace<PlacementModifier>> createNamespaces() {
        final Map<String, MethodNamespace<PlacementModifier>> m = new HashMap<>();
        NeoForge.EVENT_BUS.post(new PlacedFeatureModifierEvent(m, PlacementModifiers::accept));
        return Collections.unmodifiableMap(m);
    }

    private final Map<String, MethodNamespace<PlacementModifier>> builder;
    private final Consumer<PlacementModifier> ret;

    public PlacedFeatureModifierEvent(Map<String, MethodNamespace<PlacementModifier>> builder, Consumer<PlacementModifier> ret) {
        this.builder = builder;
        this.ret = ret;
    }

    /**
     * Create a new namespace to register synthetic functions to.
     * See {@link SyntheticFunctionEvent} for methods for creating args
     */
    @Override
    public NamespaceRegistrar<PlacementModifier> namespace(String namespace) {
        return builder.computeIfAbsent(namespace, n -> new MethodNamespace<>(n, ret));
    }
}
