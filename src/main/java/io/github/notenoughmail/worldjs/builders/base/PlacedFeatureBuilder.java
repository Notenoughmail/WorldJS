package io.github.notenoughmail.worldjs.builders.base;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.CustomJavaToJsWrapper;
import io.github.notenoughmail.worldjs.PlacedFeatureModifierEvent;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class PlacedFeatureBuilder extends BuilderBase<PlacedFeature> {

    public transient final List<PlacementModifier> modifiers;
    public transient ResourceKey<ConfiguredFeature<?, ?>> configuredFeature;

    public PlacedFeatureBuilder(ResourceLocation id) {
        super(id);
        modifiers = new ArrayList<>();
    }

    public PlacedFeatureBuilder configuredFeature(ResourceKey<ConfiguredFeature<?, ?>> feature) {
        configuredFeature = feature;
        return this;
    }

    public PlacedFeatureBuilder modifier(PlacementModifier modifier) {
        modifiers.add(modifier);
        return this;
    }

    public PlacedFeatureBuilder jsonModifier(JsonElement json) {
        return modifier(PlacementModifier.CODEC.decode(RegistryAccessContainer.current.json(), json).getOrThrow().getFirst());
    }

    public PlacedFeatureBuilder modifiers(Consumer<Modifiers> modifiers) {
        Util.make(new Modifiers(), modifiers);
        return this;
    }

    @Override
    public PlacedFeature createObject() {
        return new PlacedFeature(
                Holder.Reference.createStandAlone(
                        RegistryAccessContainer.current.access().lookupOrThrow(Registries.CONFIGURED_FEATURE),
                        Objects.requireNonNull(configuredFeature, () -> "Placed feature '%s' must define a configured feature to place".formatted(id))
                ),
                modifiers
        );
    }

    public class Modifiers implements CustomJavaToJsWrapper {

        private final Map<String, PlacedFeatureModifierEvent.ModifierNamespace> namespaces = PlacedFeatureModifierEvent.getNamespaces(modifiers::add);

        @Override
        public Scriptable convertJavaToJs(Context cx, Scriptable scope, TypeInfo staticType) {
            return new NativeJavaObject(scope, this, staticType, cx) {

                @Override
                public Object get(Context cx, String name, Scriptable start) {
                    final PlacedFeatureModifierEvent.ModifierNamespace namespace = namespaces.get(name);
                    if (namespace != null) return namespace;
                    return super.get(cx, name, start);
                }
            };
        }
    }
}
