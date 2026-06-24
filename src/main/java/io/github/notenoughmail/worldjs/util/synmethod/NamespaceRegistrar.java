package io.github.notenoughmail.worldjs.util.synmethod;

import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.rhino.type.ClassTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.type.TypeStringContext;
import dev.latvian.mods.rhino.util.HideFromJS;
import io.github.notenoughmail.worldjs.WorldJS;
import org.jetbrains.annotations.ApiStatus;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A synthetic function namespace. Analogous to {@link dev.latvian.mods.kubejs.recipe.schema.RecipeNamespace RecipeNamespace}
 * but much less complex and for things other than recipes
 * <p>
 * <strong>Note</strong>: For ease of implementation and
 * script simplicity, synthetic functions with the same name
 * are differentiated purely by argument length, thus
 * <pre>{@code
 * namespace
 *      .synFunc(<single_arg_of_type_A>)
 * namespace
 *      .synFunc(<single_arg_of_type_B>)
 * }</pre> is impossible
 * @param <R> The result (though not necessarily the {@code return}) of the synthetic functions
 */
@HideFromJS
public interface NamespaceRegistrar<R> {

    /**
     * Registers a dynamic function to the namespace
     * @param name The name of the synthetic function to call
     * @param arguments The argument information
     * @param method The {@link R} constructor. The given array will have the same length as args
     *               given in {@code arguments} and they will be of the specified type in the same
     *               order as the args are given
     * @param functionDescription ProbeJS information about the synthetic function
     */
    NamespaceRegistrar<R> register(
            String name,
            Args arguments,
            Method<Object[], ? extends R> method,
            String functionDescription
    );

    /**
     * Registers a unit (no argument) function to the namespace
     * @param name The name of the synthetic function to call
     * @param unit The {@link R} instance to 'construct'
     * @param functionDescription ProbeJS information about the synthetic function
     */
    default NamespaceRegistrar<R> unit(
            String name,
            R unit,
            String functionDescription
    ) {
        return register(
                name,
                Args.EMPTY,
                o -> unit,
                functionDescription
        );
    }

    /**
     * Registers a unit (no argument) function to the namespace
     * @param name The name of the synthetic function to call
     * @param unit The {@link R} constructor
     * @param functionDescription ProbeJS information about the synthetic function
     */
    default NamespaceRegistrar<R> unit(
            String name,
            Supplier<? extends R> unit,
            String functionDescription
    ) {
        return register(
                name,
                Args.EMPTY,
                o -> unit.get(),
                functionDescription
        );
    }

    /**
     * Registers a dynamic function with a single argument to the namespace
     * @param name The name of the synthetic function to call
     * @param argument The argument information
     * @param method The {@link R} constructor
     * @param functionDescription ProbeJS information about the synthetic function
     * @param <T> The argument type of the synthetic function
     */
    default <T> NamespaceRegistrar<R> registerSingleArg(
            String name,
            Args.Arg argument,
            Method<T, ? extends R> method,
            String functionDescription
    ) {
        return register(
                name,
                new Args(List.of(argument)),
                a -> method.invoke(Cast.to(a[0])),
                functionDescription
        );
    }

    /**
     * Registers a dynamic function with a single argument to the namespace
     * @param name The name of the synthetic function to call
     * @param argumentName The name of the argument
     * @param argumentType The {@link TypeInfo} describing the argument
     * @param argumentDescription ProbeJS information about the argument
     * @param method The {@link R} constructor
     * @param functionDescription ProbeJS information about the synthetic function
     * @param <T> The argument type of the synthetic function
     */
    default <T> NamespaceRegistrar<R> registerSingleArg(
            String name,
            String argumentName,
            TypeInfo argumentType,
            String argumentDescription,
            Method<T, ? extends R> method,
            String functionDescription
    ) {
        return registerSingleArg(
                name,
                new Args.Arg(argumentName, argumentType, argumentDescription),
                method,
                functionDescription
        );
    }

    /**
     * Registers a dynamic function with a single argument to the namespace
     * @param name The name of the synthetic argument to call
     * @param argumentName The name of the argument
     * @param argumentType The argument type
     * @param argumentDescription ProbeJS information about the argument
     * @param method The {@link R} constructor
     * @param functionDescription ProbeJS information about the synthetic function
     * @param <T> The argument type of the synthetic function
     */
    default <T> NamespaceRegistrar<R> registerSingleArg(
            String name,
            String argumentName,
            Class<T> argumentType,
            String argumentDescription,
            Method<T, ? extends R> method,
            String functionDescription
    ) {
        return registerSingleArg(
                name,
                argumentName,
                TypeInfo.of(argumentType),
                argumentDescription,
                method,
                functionDescription
        );
    }

    /**
     * Prints all the registered methods in the namespace to the console in a Markdown format with TypeScript type hints.
     * Includes probe docs for the method and each param
     */
    default void printAll() {
        WorldJS.LOGGER.info("Dumping methods registered to {}", this);

        final TypeStringContext PRINT_CTX = new TypeStringContext() {
            @Override
            public void appendClassName(StringBuilder sb, ClassTypeInfo type) {
                sb.append(type.asClass().getSimpleName());
            }
        };

        final StringBuilder builder = new StringBuilder("\n");

        for (Map.Entry<String, MethodNamespace.Functions<R>> entry : getAll()) {
            final String methodName = entry.getKey();
            final MethodNamespace.Functions<R> funcs = entry.getValue();

            for (MethodNamespace.Function<R> func : funcs.functions.values()) {
                builder.append("- `.").append(methodName).append("(");

                if (func.args().length() != 0) {
                    final Iterator<Args.Arg> iterator = func.args().args().iterator();
                    while (iterator.hasNext()) {
                        final Args.Arg arg = iterator.next();
                        builder.append(arg.name()).append(": ").append(PRINT_CTX.toString(arg.type()));
                        if (iterator.hasNext()) {
                            builder.append(", ");
                        }
                    }
                }

                builder.append(")`: ").append(func.probeDesc());

                for (Args.Arg arg : func.args().args()) {
                    builder.append("\n\t- `")
                            .append(arg.name())
                            .append(": ")
                            .append(PRINT_CTX.toString(arg.type()))
                            .append("`: ")
                            .append(arg.desc());
                }

                builder.append("\n");
            }
        }
        WorldJS.LOGGER.info(builder.toString());
    }

    @ApiStatus.Internal
    Set<Map.Entry<String, MethodNamespace.Functions<R>>> getAll();
}
