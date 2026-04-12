package io.github.notenoughmail.worldjs.builders.base;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.Lazy;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.CustomJavaToJsWrapper;
import dev.latvian.mods.rhino.util.HideFromJS;
import io.github.notenoughmail.worldjs.PlacedFeatureModifierEvent;
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
    public transient Holder.Reference<ConfiguredFeature<?, ?>> configuredFeature;

    public PlacedFeatureBuilder(ResourceLocation id) {
        super(id);
        modifiers = new ArrayList<>();
    }

    @HideFromJS
    public void configuredFeature(ConfiguredFeatureBuilder<?> configuredFeatureBuilder) {
        configuredFeature = Holder.Reference.createStandAlone(
                RegistryAccessContainer.current.access().lookupOrThrow(Registries.CONFIGURED_FEATURE),
                ResourceKey.create(Registries.CONFIGURED_FEATURE, configuredFeatureBuilder.id)
        );
    }

    @Info("The configured feature the placed feature will place")
    public PlacedFeatureBuilder configuredFeature(Holder.Reference<ConfiguredFeature<?, ?>> feature) {
        configuredFeature = feature;
        return this;
    }

    @Info("Add an arbitrary placement modifier")
    public PlacedFeatureBuilder modifier(PlacementModifier modifier) {
        modifiers.add(modifier);
        return this;
    }

    @Info("Add an arbitrary placement modifier from its json representation")
    public PlacedFeatureBuilder jsonModifier(JsonElement json) {
        return modifier(PlacementModifier.CODEC.decode(RegistryAccessContainer.current.json(), json).getOrThrow().getFirst());
    }

    @Info("Add placement modifiers")
    public PlacedFeatureBuilder modifiers(Consumer<Modifiers> modifiers) {
        Modifiers.accept(this::modifier, modifiers);
        return this;
    }

    @Override
    public PlacedFeature createObject() {
        return new PlacedFeature(
                Objects.requireNonNull(
                        configuredFeature,
                        () -> "Placed feature '%s' must define a configured feature to place".formatted(id)
                ),
                modifiers
        );
    }

    public enum Modifiers implements CustomJavaToJsWrapper {
        INSTANCE;

        public static Consumer<PlacementModifier> modifierRet;

        public static final Lazy<Map<String, PlacedFeatureModifierEvent.ModifierNamespace>> NAMESPACES = Lazy.of(PlacedFeatureModifierEvent::createNamespaces);

        public static void accept(Consumer<PlacementModifier> ret, Consumer<Modifiers> source) {
            modifierRet = ret;
            source.accept(INSTANCE);
            modifierRet = null;
        }

        public static void accept(PlacementModifier mod) {
            if (modifierRet != null) modifierRet.accept(mod);
        }

        @Override
        public Scriptable convertJavaToJs(Context cx, Scriptable scope, TypeInfo staticType) {
            return new NativeJavaObject(scope, this, staticType, cx) {

                @Override
                public Object get(Context cx, String name, Scriptable start) {
                    final PlacedFeatureModifierEvent.ModifierNamespace namespace = NAMESPACES.get().get(name);
                    if (namespace != null) return namespace;
                    return super.get(cx, name, start);
                }
            };
        }
    }
}
