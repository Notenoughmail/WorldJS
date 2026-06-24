package io.github.notenoughmail.worldjs.util;

import com.machinezoo.noexception.throwing.ThrowingRunnable;
import dev.latvian.mods.kubejs.holder.HolderWrapper;
import dev.latvian.mods.kubejs.holder.NamespaceHolderSet;
import dev.latvian.mods.kubejs.holder.RegExHolderSet;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.RegExpKJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.registries.holdersets.OrHolderSet;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public interface ServerRegistryHolderSet<R> {

    ServerRegistryHolderSet<?> EMPTY = HolderSet::empty;

    @HideFromJS
    default HolderSet<R> verify(Runnable err) {
        final HolderSet<R> set = convert();
        if (set instanceof HolderSet.Direct<R> dir && dir.size() == 0) {
            err.run();
        }
        return set;
    }

    @HideFromJS
    HolderSet<R> convert();

    // Only intended for server registries, so it's unlikely to have any existing Holder(Set)s or R instances
    static ServerRegistryHolderSet<?> wrap(Context ctx, Object from, TypeInfo type) {
        final TypeInfo param = type.param(0);
        final Registry<?> registry = Cast.<KubeJSContext>to(ctx).lookupRegistry(param, from);

        final ServerRegistryHolderSet<?> simple = simpleWrap(registry, from);
        if (simple != null) {
            return simple;
        }

        if (from instanceof Iterable<?> itr) {
            final Stream.Builder<HolderSet<?>> directs = Stream.builder();
            final List<HolderSet<?>> complex = new ArrayList<>();

            for (Object obj : itr) {
                final HolderSet<?> wrapped = wrap(ctx, obj, type).convert();

                if (wrapped instanceof HolderSet.Direct<?> direct) {
                    directs.accept(direct);
                } else {
                    complex.add(wrapped);
                }
            }

            final List<Holder<?>> compressedDirects = directs.build().<Holder<?>>flatMap(HolderSet::stream).distinct().toList();

            if (compressedDirects.isEmpty()) {
                return switch (complex.size()) {
                    case 0 -> EMPTY;
                    case 1 -> simple(complex.getFirst());
                    default -> simple(new OrHolderSet(complex));
                };
            } else {
                if (complex.isEmpty()) {
                    return simple(dir(compressedDirects));
                } else {
                    complex.add(dir(compressedDirects));
                    return simple(new OrHolderSet(complex));
                }
            }
        } else {
            return () -> Cast.to(HolderWrapper.wrapSet(Cast.to(ctx), from, param));
        }
    }

    @Nullable
    private static <R> ServerRegistryHolderSet<?> simpleWrap(Registry<R> registry, Object from) {
        return switch (from) {
            case HolderSet<?> set -> simple(set); // In case someone constructs a modded type manually
            case Holder.Reference<?> ref when ref.key().isFor(registry.key()) -> simple(HolderSet.direct(ref));
            case NativeRegExp regex -> simple(RegExHolderSet.of(registry.asLookup(), RegExpKJS.wrap(regex)));
            case Pattern regex -> simple(RegExHolderSet.of(registry.asLookup(), regex));
            case TagKey<?> tag when tag.isFor(registry.key()) -> simple(registry.getOrCreateTag(Cast.to(tag)));
            case ResourceKey<?> key when key.isFor(registry.key()) -> ref(Cast.to(key), registry);
            case ResourceLocation id -> ref(
                    ResourceKey.create(registry.key(), id),
                    registry
            );
            case CharSequence cs when cs.isEmpty() -> EMPTY;
            case CharSequence cs -> {
                final String str = cs.toString();
                yield switch (str.charAt(0)) {
                    case '@' -> simple(NamespaceHolderSet.of(registry.asLookup(), str.substring(1)));
                    case '#' -> {
                        final TagKey<R> tag = TagKey.create(registry.key(), ResourceLocation.parse(str.substring(1)));
                        yield simple(registry.getOrCreateTag(tag));
                    }
                    case '/' -> simpleWrap(registry, RegExpKJS.wrap(from));
                    default -> ResourceLocation.read(str)
                            .result()
                            .map(id -> simpleWrap(registry, id))
                            .orElse(null);
                };
            }
            case null, default -> null;
        };
    }

    static <R> ServerRegistryHolderSet<R> simple(HolderSet<R> set) {
        return () -> set;
    }

    @SuppressWarnings("all")
    private static <R> ServerRegistryHolderSet<R> orEmpty(Optional<? extends HolderSet<R>> set) {
        return set.map(ServerRegistryHolderSet::simple).orElse(Cast.to(EMPTY));
    }

    private static <R> ServerRegistryHolderSet<R> ref(ResourceKey<R> key, Registry<R> reg) {
        return simple(HolderSet.direct(Holder.Reference.createStandAlone(reg.holderOwner(), key)));
    }

    private static <R> HolderSet<R> dir(List<Holder<?>> holders) {
        return HolderSet.direct(Cast.<List<Holder<R>>>to(holders));
    }
}
