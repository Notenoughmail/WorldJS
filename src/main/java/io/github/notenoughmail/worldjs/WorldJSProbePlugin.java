package io.github.notenoughmail.worldjs;

import com.google.gson.JsonObject;
import dev.latvian.mods.rhino.type.TypeInfo;
import io.github.notenoughmail.worldjs.util.PlacementModifiers;
import io.github.notenoughmail.worldjs.util.WeightedValue;
import io.github.notenoughmail.worldjs.util.synmethod.Args;
import io.github.notenoughmail.worldjs.util.synmethod.MethodNamespace;
import moe.wolfgirl.probejs.plugin.ProbeJSPlugin;
import moe.wolfgirl.probejs.plugin.builtins.alias.RecordTypes;
import moe.wolfgirl.probejs.typescript.ClassPath;
import moe.wolfgirl.probejs.typescript.Documents;
import moe.wolfgirl.probejs.typescript.base.AliasRegistrar;
import moe.wolfgirl.probejs.typescript.document.Members;
import moe.wolfgirl.probejs.typescript.document.Types;
import moe.wolfgirl.probejs.typescript.document.base.Code;
import moe.wolfgirl.probejs.typescript.document.base.CommentableCode;
import moe.wolfgirl.probejs.typescript.document.base.Type;
import moe.wolfgirl.probejs.typescript.document.builders.ClassBuilder;
import moe.wolfgirl.probejs.typescript.document.builders.MethodBuilder;
import moe.wolfgirl.probejs.typescript.document.members.MethodDecl;
import moe.wolfgirl.probejs.typescript.document.types.special.ObjectType;
import moe.wolfgirl.probejs.typescript.transpiler.TypeConverter;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.material.Fluid;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import static io.github.notenoughmail.worldjs.util.Types.*;

public class WorldJSProbePlugin extends ProbeJSPlugin {

    static {
        RecordTypes.SKIP_RECORDS.add(WeightedValue.class);
    }

    @Override
    public Set<Class<?>> provideClassForDiscovery() {
        return Set.of(
                WeightedValue.class,
                PlacementModifiers.class,
                HeightProvider.class,
                BlockPredicate.class,
                OreConfiguration.TargetBlockState.class,
                BlockStateProvider.class,
                VerticalAnchor.class
        );
    }

    @Override
    public void modifyClasses(Documents.ClassAccessor classDocuments) {
        final Map<String, MethodNamespace<PlacementModifier>> modifiers = PlacementModifiers.NAMESPACES.get();
        final ClassPath modifiersClazz = path(PlacementModifiers.class);
        final ClassBuilder builder = Members.clazz(modifiersClazz);
        classDocuments.removeClassDocument(modifiersClazz, null);

        for (Map.Entry<String, MethodNamespace<PlacementModifier>> entry : modifiers.entrySet()) {
            final String name = entry.getKey();
            final MethodNamespace<PlacementModifier> namespace = entry.getValue();
            final Namespace member = new Namespace(name);
            namespace(classDocuments.converter, member::method, namespace);
            builder.member(member);
        }

        classDocuments.addClassDocument(modifiersClazz, builder.build());
    }

    private static void namespace(TypeConverter converter, Consumer<MethodDecl> methods, MethodNamespace<PlacementModifier> namespace) {
        for (Map.Entry<String, MethodNamespace.Functions<PlacementModifier>> entry : namespace.functions.entrySet()) {
            final String name = entry.getKey();
            final MethodNamespace.Functions<PlacementModifier> functions = entry.getValue();

            for (MethodNamespace.Function<PlacementModifier> func : functions.functions.values()) {
                methods.accept(buildMethod(converter, func, name));
            }
        }
    }

    private static MethodDecl buildMethod(TypeConverter converter, MethodNamespace.Function<PlacementModifier> func, String name) {
        final MethodBuilder builder = Members.method(name);
        builder.returnType(Types.THIS);

        final int argLength = func.args().length();
        if (argLength != 0) {
            for (int i = 0 ; i < argLength ; i++) {
                final Args.Arg arg = func.args().get(i);
                builder.param(arg.name(), converter.convertType(arg.type()));
            }
        }

        final MethodDecl method = builder.build();
        if (!func.probeDesc().isEmpty()) {
            method.addComments(func.probeDesc());
        }
        if (argLength != 0) {
            for (var arg : func.args().args()) {
                if (!arg.desc().isEmpty()) {
                    method.addComments("@param %s %s".formatted(arg.name(), arg.desc()));
                }
            }
        }

        return method;
    }

    @Override
    public void addTypeAlias(AliasRegistrar r) {
        final TypeConverter converter = new TypeConverter();
        final Type primInt = converter.convertType(INT);
        final Type json = converter.convertType(TypeInfo.of(JsonObject.class));
        final Type blockState = converter.convertType(BLOCK_STATE);
        final Type block = converter.convertType(BLOCK);
        final Type bool = converter.convertType(BOOL);

        {
            // WeightedValue
            final ClassPath clazz = path(WeightedValue.class);
            final Type valueType = Types.variable("T");

            // Skip the array & callback forms because I do not want to encourage those
            r.addInputAlias(
                    clazz,
                    obj(b -> b.param("weight", true, primInt)
                              .param("value", valueType))

            );
            r.addInputAlias(clazz, valueType);
        }

        {
            // VerticalAnchor
            final ClassPath clazz = path(VerticalAnchor.class);

            r.addInputAlias(clazz, primInt);
            r.addInputAlias(clazz, literal("bottom"));
            r.addInputAlias(clazz, literal("top"));
            r.addInputAlias(clazz, literal("-"));
            r.addInputAlias(clazz, literal("zero"));
            r.addInputAlias(clazz, obj(b -> b.param("absolute", primInt)));
            r.addInputAlias(clazz, obj(b -> b.param("above_bottom", primInt)));
            r.addInputAlias(clazz, obj(b -> b.param("below_top", primInt)));
            r.addInputAlias(clazz, json);
        }

        {
            // HeightProvider
            final ClassPath clazz = path(HeightProvider.class);
            final Type va = converter.convertType(VERTICAL_ANCHOR);

            r.addInputAlias(clazz, primInt);
            r.addInputAlias(clazz, va);
            r.addInputAlias(clazz, converter.convertType(WEIGHTED_HEIGHT_PROVIDER_LIST));
            obj(r, clazz, "uniform", b ->
                    b.param("min", va)
                     .param("max", va)
            );
            r.addInputAlias(clazz, obj(b ->
                    b.param("constant", va)
            ));
            obj(r, clazz, "trapezoid", b ->
                    b.param("min", va)
                     .param("max", va)
                     .param("plateau", true, primInt)
            );
            r.addInputAlias(clazz, obj(b ->
                    b.param("weighted", converter.convertType(WEIGHTED_HEIGHT_PROVIDER_LIST))
            ));
            obj(r, clazz, "biased", b ->
                    b.param("min", va)
                     .param("max", va)
                     .param("inner", true, primInt)
                     .param("extreme", true, bool)
            );
            r.addInputAlias(clazz, json);
        }

        {
            // BlockStateProvider
            final ClassPath clazz = path(BlockStateProvider.class);

            r.addInputAlias(clazz, block);
            r.addInputAlias(clazz, blockState);
            r.addInputAlias(clazz, converter.convertType(WEIGHTED_BLOCK_STATE_LIST));
            r.addInputAlias(clazz, obj(b ->
                    b.param("simple", blockState)
            ));
            r.addInputAlias(clazz, obj(b ->
                    b.param("block", blockState)
            ));
            r.addInputAlias(clazz, obj(b ->
                    b.param("rotate", block)
            ));
            obj(r, clazz, "randomized_int", b ->
                    b.param("property", Types.STRING)
                     .param("values", converter.convertType(INT_PROVIDER))
                     .param("source", converter.convertType(BLOCK_STATE_PROVIDER))
            );
            r.addInputAlias(clazz, obj(b ->
                    b.param("weighted", converter.convertType(WEIGHTED_BLOCK_STATE_LIST))
            ));
            r.addInputAlias(clazz, json);
        }

        {
            // OreConfiguration$TargetBlockState
            final ClassPath clazz = path(OreConfiguration.TargetBlockState.class);
            final Type ruleTest = converter.convertType(TypeInfo.of(RuleTest.class));

            r.addInputAlias(clazz, obj(b ->
                    b.param("target", ruleTest)
                     .param("state", blockState)
            ));
        }

        {
            // BlockPredicate
            final ClassPath clazz = path(BlockPredicate.class);
            final Type list = converter.convertType(LIST_BLOCK_PREDICATE);
            final Type offset = converter.convertType(VEC3I);
            final Type tag = converter.convertType(BLOCK_TAG);

            final UnaryOperator<ObjectType.Builder> off = b -> b.param("offset", true, offset);

            r.addInputAlias(clazz, block);
            r.addInputAlias(clazz, Fluid.class);
            r.addInputAlias(clazz, tag);
            r.addInputAlias(clazz, list);
            r.addInputAlias(clazz, bool);
            r.addInputAlias(clazz, obj(b ->
                    b.param("not", converter.convertType(BLOCK_PREDICATE))
            ));
            r.addInputAlias(clazz, obj(b ->
                    b.param("all", list)
            ));
            r.addInputAlias(clazz, obj(b ->
                    b.param("any", list)
            ));
            obj(r, clazz, "blocks", b ->
                    off.apply(b)
                       .param("match", converter.convertType(BLOCK_HOLDER_SET))
            );
            obj(r, clazz, "tag", b ->
                    off.apply(b)
                       .param("match", tag)
            );
            obj(r, clazz, "fluids", b ->
                    off.apply(b)
                       .param("match", converter.convertType(FLUID_HOLDER_SET))
            );
            obj(r, clazz, "replaceable", off::apply);
            obj(r, clazz, "would_survive", b ->
                    off.apply(b)
                       .param("state", blockState)
            );
            obj(r, clazz, "has_sturdy_face", b ->
                    off.apply(b)
                       .param("direction", converter.convertType(DIRECTION))
            );
            obj(r, clazz, "solid", off::apply);
            obj(r, clazz, "no_fluid", off::apply);
            obj(r, clazz, "inside_world", off::apply);
            obj(r, clazz, "unobstructed", off::apply);
        }
    }

    private static Type literal(String str) {
        return Types.literal(str);
    }

    private static Type obj(Consumer<ObjectType.Builder> builder) {
        return Types.object(builder);
    }

    private static void obj(AliasRegistrar r, ClassPath clazz, String name, Consumer<ObjectType.Builder> builder) {
        r.addInputAlias(clazz, obj(b -> b.param(name, obj(builder))));
    }

    private static ClassPath path(Class<?> clazz) {
        return new ClassPath(clazz);
    }

    static class Namespace extends Code {
        private final List<MethodDecl> methods;
        private final String name;

        Namespace(String name) {
            this.name = name;
            methods = new ArrayList<>();
        }

        public void method(MethodDecl method) {
            methods.add(method);
        }

        @Override
        public Set<ClassPath> getImports() {
            Set<ClassPath> imports = new HashSet<>();
            for (MethodDecl method : methods) imports.addAll(method.getImports());
            return imports;
        }

        @Override
        public List<String> format(int indent) {
            final List<String> lines = new ArrayList<>();
            lines.add(" ".repeat(indent) + "%s: {".formatted(name));
            for (MethodDecl method : methods) {
                lines.addAll(CommentableCode.format(method, indent + 4));
            }
            lines.add(" ".repeat(indent) + "}");
            return lines;
        }
    }
}
