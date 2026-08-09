package io.github.notenoughmail.worldjs.util;

import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Function;

@HideFromJS
public interface Validations {

    static int assertPositive(int val, String name) {
        if (val < 1)
            throw new IllegalArgumentException("'" + name + "' must be > 0");
        return val;
    }

    static int assertNonNegative(int val, String name) {
        if (val < 0)
            throw new IllegalArgumentException("'" + name + "' must be >= 0");
        return val;
    }

    static int assertRange(int val, int min, int max, String name) {
        if (val < min || val > max)
            throw new IllegalArgumentException("'" + name + "' must be in the range [" + min + ", " + max + "] but was " + val);
        return val;
    }

    static int assertNotGreaterThan(int check, int against, String checkName, @Nullable String againstName, SourceLine source) {
        if (check > against)
            throw exception(source, "'%s' cannot be greater than '%s'".formatted(checkName, againstName == null ? against : againstName));
        return check;
    }

    static int assertIsMultipleOf(int val, int multiple, String name, SourceLine source) {
        if (val % multiple != 0)
            throw exception(source, "'%s' must be a multiple of %s!".formatted(name, multiple));
        return val;
    }

    static float assertRange(float val, float min, float max, String name) {
        if (val < min || val > max)
            throw new IllegalArgumentException("'%s' must be in the range [%.2f, %.2f] but was %.2f".formatted(name, min, max, val));
        return val;
    }

    static double assertRange(double val, double min, double max, String name) {
        if (val < min || val > max)
            throw new IllegalArgumentException("'%s' must be in the range [%.2f, %.2f] but was %.2f".formatted(name, min, max, val));
        return val;
    }

    static float assertUnit(float val, String name) {
        return assertRange(val, 0f, 1f, name);
    }

    static double assertUnit(double val, String name) {
        return assertRange(val, 0D, 1D, name);
    }

    static IntProvider assertRange(IntProvider provider, int min, int max, String name) {
        if (provider.getMinValue() < min || provider.getMaxValue() > max)
            throw new IllegalArgumentException("'" + name + "' must be in the range [" + min + ", " + max + "] but was [" + provider.getMinValue() + ", " + provider.getMaxValue() + "]");
        return provider;
    }

    static IntProvider assertNonNegative(IntProvider provider, String name) {
        if (provider.getMinValue() < 0)
            throw new IllegalArgumentException("'" + name + "' must be >= 0");
        return provider;
    }

    static IntProvider assertPositive(IntProvider provider, String name) {
        if (provider.getMinValue() < 1)
            throw new IllegalArgumentException("'" + name + "' must be > 0");
        return provider;
    }

    static FloatProvider assertRange(FloatProvider provider, float min, float max, String name) {
        if (provider.getMinValue() < min || provider.getMaxValue() > max)
            throw new IllegalArgumentException("'%s' must be in the range [%.2f, %.2f] but was [%.2f, %.2f]".formatted(name, min, max, provider.getMinValue(), provider.getMaxValue()));
        return provider;
    }

    @Contract("null, _, _ -> fail; _, _, _ -> !null")
    static <T> T notNull(T t, String name, SourceLine source) {
        if (t == null)
            throw exception(source, "'" + name + "' must be defined!");
        return t;
    }

    static <C extends Collection<? extends T>, T> C notEmpty(C collection, String name, SourceLine source) {
        if (collection.isEmpty()) {
            throw exception(source, "'" + name + "' must not be empty!");
        }
        return collection;
    }

    static <T> T validate(T t, Function<T, @Nullable String> errorMessageFunction, SourceLine source) {
        final String str = errorMessageFunction.apply(t);
        if (str == null) return t;
        throw exception(source, str);
    }

    static <T> T validate(T t, Function<T, @Nullable String> errorMessageFunction, Function<String, KubeRuntimeException> messageWrapper) {
        final String str = errorMessageFunction.apply(t);
        if (str == null) return t;
        throw messageWrapper.apply(str);
    }

    static KubeRuntimeException exception(SourceLine source, String message) {
        return new KubeRuntimeException(message).source(source);
    }

    static KubeRuntimeException exception(Context ctx, String message) {
        return exception(SourceLine.of(ctx), message);
    }
}
