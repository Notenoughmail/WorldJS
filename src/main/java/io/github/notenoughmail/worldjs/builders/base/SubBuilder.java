package io.github.notenoughmail.worldjs.builders.base;

import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.util.Validations;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@ReturnsSelf
@HideFromJS
public abstract class SubBuilder<T> {

    public static <C, B extends SubBuilder<? extends T>, T> T build(
            Context ctx,
            ResourceLocation typeId,
            C constructorArg,
            Consumer<B> builder,
            Supplier<Map<ResourceLocation, SubBuilderInfo<C, ? extends B>>> types,
            String typeName
    ) {
        final SourceLine line = SourceLine.of(ctx);
        final SubBuilderInfo<C, ? extends B> builderInfo = types.get().get(typeId);
        if (builderInfo == null)
            throw Validations.exception(line, "Unknown " + typeName + " type: '" + typeId + "'");
        final B b = builderInfo.constructor().apply(constructorArg);
        b.sourceLine = line;
        builder.accept(b);
        return b.create();
    }

    protected SourceLine sourceLine;

    protected abstract T create();

    @HideFromJS
    public record SubBuilderInfo<C, T>(
            TypeInfo type,
            Function<C, T> constructor
    ) {}
}

