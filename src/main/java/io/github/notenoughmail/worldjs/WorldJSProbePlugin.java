package io.github.notenoughmail.worldjs;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.rhino.type.JSStringConstantTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import io.github.notenoughmail.worldjs.builders.base.SubBuilder;
import io.github.notenoughmail.worldjs.types.assist.ClimateParameterListBuilder;
import io.github.notenoughmail.worldjs.util.PlacementModifiers;
import io.github.notenoughmail.worldjs.util.ServerRegistryHolderSet;
import io.github.notenoughmail.worldjs.util.WeightedValue;
import io.github.notenoughmail.worldjs.util.synmethod.Args;
import io.github.notenoughmail.worldjs.util.synmethod.MethodNamespace;
import moe.wolfgirl.probejs.plugin.ProbeJSPlugin;
import moe.wolfgirl.probejs.plugin.builtins.alias.RecordTypes;
import moe.wolfgirl.probejs.typescript.ClassPath;
import moe.wolfgirl.probejs.typescript.Documents;
import moe.wolfgirl.probejs.typescript.base.AliasRegistrar;
import moe.wolfgirl.probejs.typescript.document.ClassDecl;
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
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.material.Fluid;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.notenoughmail.worldjs.util.Types.*;

public class WorldJSProbePlugin extends ProbeJSPlugin {

    static {
        RecordTypes.SKIP_RECORDS.add(WeightedValue.class);
        RecordTypes.SKIP_RECORDS.add(ClimateParameterListBuilder.Entry.class);
    }

    @Override
    public Set<Class<?>> provideClassForDiscovery() {
        return Stream.of(
                Stream.of(
                        WeightedValue.class,
                        PlacementModifiers.class,
                        HeightProvider.class,
                        BlockPredicate.class,
                        OreConfiguration.TargetBlockState.class,
                        BlockStateProvider.class,
                        VerticalAnchor.class
                ),
                SubBuilder.SUB_BUILDER_MAPS.keySet()
                        .stream(),
                SubBuilder.SUB_BUILDER_MAPS.values()
                        .stream()
                        .flatMap(Collection::stream)
                        .map(SubBuilder.SubBuilderMap::types)
                        .map(Supplier::get)
                        .map(Map::values)
                        .flatMap(Collection::stream)
                        .map(SubBuilder.SubBuilderType::type)
                        .map(TypeInfo::asClass)
        ).flatMap(Function.identity())
                .collect(Collectors.toSet());
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
        final Type primDoub = converter.convertType(DOUB);
        final Type primFloat = converter.convertType(TypeInfo.PRIMITIVE_FLOAT);
        final Type json = converter.convertType(TypeInfo.of(JsonObject.class));
        final Type blockState = converter.convertType(BLOCK_STATE);
        final Type block = converter.convertType(BLOCK);
        final Type bool = converter.convertType(BOOL);
        final Type conditionSource = converter.convertType(CONDITION_SOURCE);

        {
            // WeightedValue
            final ClassPath clazz = path(WeightedValue.class);
            final Type valueType = Types.raw("T");

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
            singleObj(r, clazz, "absolute", primInt);
            singleObj(r, clazz, "above_bottom", primInt);
            singleObj(r, clazz, "below_top", primInt);
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
            singleObj(r, clazz, "weighted", converter.convertType(WEIGHTED_HEIGHT_PROVIDER_LIST));
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
            final Type weight = converter.convertType(WEIGHTED_BLOCK_STATE_LIST);

            r.addInputAlias(clazz, block);
            r.addInputAlias(clazz, blockState);
            r.addInputAlias(clazz, weight);
            singleObj(r, clazz, "simple", blockState);
            singleObj(r, clazz, "block", blockState);
            singleObj(r, clazz, "rotate", block);
            obj(r, clazz, "randomized_int", b ->
                    b.param("property", Types.STRING)
                     .param("values", converter.convertType(INT_PROVIDER))
                     .param("source", converter.convertType(BLOCK_STATE_PROVIDER))
            );
            singleObj(r, clazz, "weighted", weight);
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
            singleObj(r, clazz, "not", converter.convertType(BLOCK_PREDICATE));
            singleObj(r, clazz, "all", list);
            singleObj(r, clazz, "any", list);
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

        {
            // SurfaceRules$RuleSource
            final ClassPath clazz = path(SurfaceRules.RuleSource.class);
            final Type list = converter.convertType(list(TypeInfo.of(SurfaceRules.RuleSource.class)));

            r.addInputAlias(clazz, literal("badlands"));
            r.addInputAlias(clazz, list);
            anonObj(r, clazz, "badlands");
            singleObj(r, clazz, "sequence", list);
            singleObj(r, clazz, "block", blockState);
            obj(r, clazz, "condition", b ->
                    b.param("if_true", conditionSource)
                     .param("then_run", Types.clazz(clazz))
            );
            r.addInputAlias(clazz, blockState);
            r.addInputAlias(clazz, json);
        }

        {
            // SurfaceRules$ConditionSource
            final ClassPath clazz = path(SurfaceRules.ConditionSource.class);
            final Type biomeList = converter.convertType(list(BIOME_RES_KEY));
            final Type verticalAnchor = converter.convertType(VERTICAL_ANCHOR);

            r.addInputAlias(clazz, literal("above_preliminary_surface"));
            r.addInputAlias(clazz, literal("hole"));
            r.addInputAlias(clazz, literal("temperature"));
            r.addInputAlias(clazz, literal("steep"));
            r.addInputAlias(clazz, biomeList);
            singleObj(r, clazz, "biome", biomeList);
            obj(r, clazz, "noise_threshold", b ->
                    b.param("noise", converter.convertType(NOISE_PARAMS_RES_KEY))
                     .param("min_threshold", primDoub)
                     .param("max_threshold", true, primDoub)
            );
            obj(r, clazz, "vertical_gradient", b ->
                    b.param("random_name", converter.convertType(TypeInfo.of(ResourceLocation.class)))
                     .param("true_at_and_below", verticalAnchor)
                     .param("false_at_and_above", verticalAnchor)
            );
            obj(r, clazz, "y_above", b ->
                    b.param("add_stone_depth", true, bool)
                     .param("anchor", verticalAnchor)
                     .param("surface_depth_multiplier", primInt)
            );
            obj(r, clazz, "water", b ->
                    b.param("offset", primInt)
                     .param("surface_depth_multiplier", primInt)
                     .param("add_stone_depth", true, bool)
            );
            anonObj(r, clazz, "temperature");
            anonObj(r, clazz, "steep");
            anonObj(r, clazz, "hole");
            anonObj(r, clazz, "above_preliminary_surface");
            singleObj(r, clazz, "not", conditionSource);
            obj(r, clazz, "stone_depth", b ->
                    b.param("offset", primInt)
                     .param("add_surface_depth", true, bool)
                     .param("secondary_depth_range", primInt)
                     .param("surface_type", converter.convertType(TypeInfo.of(CaveSurface.class)))
            );
            r.addInputAlias(clazz, json);
        }

        {
            // DensityFunction
            final ClassPath clazz = path(DensityFunction.class);
            final Type densityFunction = converter.convertType(DENSITY_FUNCTION);
            final Type noiseParams = converter.convertType(TypeInfo.of(Holder.Reference.class).withParams(NOISE_PARAMS));
            final Type ref = converter.convertType(TypeInfo.of(Holder.Reference.class).withParams(DENSITY_FUNCTION));

            final ClassPath splineClazz = clazz.withSuffix("$Spline");
            final Type spline = Types.clazz(splineClazz);

            r.addInputAlias(clazz, ref);
            r.addInputAlias(clazz, primDoub);
            r.addInputAlias(clazz, converter.convertType(resourceKey(DensityFunction.class)));
            anonObj(r, clazz, "blend_alpha");
            anonObj(r, clazz, "blend_offset");
            anonObj(r, clazz, "beardifier");
            obj(r, clazz, "old_blend_noise", b ->
                    b.param("xz_scale", primDoub)
                     .param("y_scale", primDoub)
                     .param("xz_factor", primDoub)
                     .param("y_factor", primDoub)
                     .param("smear_scale_multiplier", primDoub)
            );
            singleObj(r, clazz, "interpolated", densityFunction);
            singleObj(r, clazz, "flat_cache", densityFunction);
            singleObj(r, clazz, "cache_2d", densityFunction);
            singleObj(r, clazz, "cache_once", densityFunction);
            singleObj(r, clazz, "cache_all_in_cell", densityFunction);
            obj(r, clazz, "noise", b ->
                    b.param("noise", noiseParams)
                     .param("xz_scale", primDoub)
                     .param("y_scale", primDoub)
            );
            anonObj(r, clazz, "end_islands");
            obj(r, clazz, "weird_scaled_sampler", b ->
                    b.param("input", densityFunction)
                     .param("noise", noiseParams)
                     .param("rarity_value_mapper", converter.convertType(TypeInfo.of(DensityFunctions.WeirdScaledSampler.RarityValueMapper.class)))
            );
            obj(r, clazz, "shifted_noise", b ->
                    b.param("shift_x", densityFunction)
                     .param("shift_y", densityFunction)
                     .param("shift_z", densityFunction)
                     .param("xz_scale", primDoub)
                     .param("y_scale", primDoub)
                     .param("noise", noiseParams)
            );
            obj(r, clazz, "range_choice", b ->
                    b.param("input", densityFunction)
                     .param("min_inclusive", primDoub)
                     .param("max_inclusive", primDoub)
                     .param("when_in_range", densityFunction)
                     .param("when_out_of_range", densityFunction)
            );
            singleObj(r, clazz, "shift_a", noiseParams);
            singleObj(r, clazz, "shift_b", noiseParams);
            singleObj(r, clazz, "shift", noiseParams);
            singleObj(r, clazz, "blend_density", densityFunction);
            obj(r, clazz, "clamp", b ->
                    b.param("input", densityFunction)
                     .param("min_value", primDoub)
                     .param("max_value", primDoub)
            );
            singleObj(r, clazz, "abs", densityFunction);
            singleObj(r, clazz, "square", densityFunction);
            singleObj(r, clazz, "cube", densityFunction);
            singleObj(r, clazz, "half_negative", densityFunction);
            singleObj(r, clazz, "quarter_negative", densityFunction);
            singleObj(r, clazz, "squeeze", densityFunction);
            obj(r, clazz, "add", b ->
                    b.param("first", densityFunction)
                     .param("second", densityFunction)
            );
            obj(r, clazz, "mul", b ->
                    b.param("first", densityFunction)
                      .param("second", densityFunction)
            );
            obj(r, clazz, "min", b ->
                    b.param("first", densityFunction)
                     .param("second", densityFunction)
            );
            obj(r, clazz, "max", b ->
                    b.param("first", densityFunction)
                     .param("second", densityFunction)
            );
            singleObj(r, clazz, "constant", primDoub);
            singleObj(r, clazz, "spline", spline);
            obj(r, clazz, "y_clamped_gradient", b ->
                    b.param("from_y", primInt)
                     .param("to_y", primInt)
                     .param("from_value", primDoub)
                     .param("to_value", primDoub)
            );
            r.addInputAlias(clazz, json);

            {
                // DensityFunction"$Spline" & DensityFunction"$Spline$Point"

                final ClassPath pointClazz = splineClazz.withSuffix("$Point");

                r.addInputAlias(splineClazz, primFloat);
                r.addInputAlias(splineClazz, obj(b ->
                        b.param("coordinate", ref)
                         .param("value_transformer", true, converter.convertType(FLOAT_2_FLOAT))
                         .param("points", Types.arrayOf(pointClazz))
                ));

                r.addInputAlias(pointClazz, obj(b ->
                        b.param("location", primFloat)
                         .param("value", primFloat)
                         .param("derivative", primFloat)
                ));
                r.addInputAlias(pointClazz, obj(b ->
                        b.param("location", primFloat)
                         .param("value", spline)
                ));

                Documents.INSTANCE.addDocument(splineClazz, Members.clazz(splineClazz).build());
                Documents.INSTANCE.addDocument(pointClazz, Members.clazz(pointClazz).build());
            }
        }

        r.addInputAlias(ServerRegistryHolderSet.class, Types.clazz(HolderSet.class).withParams(Types.raw("R")));
    }

    private static final TypeConverter CONST_STRING_CONVERTER = new TypeConverter() {
        @Override
        public Type convertType(TypeInfo typeInfo, boolean canHaveParams, Set<String> seenVariables) {
            if (typeInfo instanceof JSStringConstantTypeInfo(String constant)) {
                return literal(constant);
            }
            return super.convertType(typeInfo, canHaveParams, seenVariables);
        }
    };

    // Can't be bothered to set up conditional mixins for an accessor
    private static final Field COMMENTABLE_CODE_COMMENTS = Util.make(() -> {
        try {
            final Field f = CommentableCode.class.getDeclaredField("comments");
            f.setAccessible(true);
            return f;
        } catch (Throwable t) {
            throw WorldJS.irrecoverableError("Cannot make method comments accessible!", t);
        }
    });

    private static List<String> removeMethodAndGetComments(ClassDecl decl, String methodName) {
        final List<String> comments = new ArrayList<>();
        decl.members.removeIf(code -> {
            if (code instanceof MethodDecl method && method.name.equals(methodName)) {
                try {
                    comments.addAll(Cast.to(COMMENTABLE_CODE_COMMENTS.get(method)));
                } catch (Throwable t) {
                    throw WorldJS.irrecoverableError("Cannot get method comments of %s".formatted(method), t);
                }
                return true;
            }
            return false;
        });
        return comments;
    }

    @Override
    public void transformClass(Documents.ClassDocument document) {
        final Set<SubBuilder.SubBuilderMap<?, ?, ?>> maps = SubBuilder.SUB_BUILDER_MAPS.get(document.classInfo().clazz());
        if (maps != null) {
            for (SubBuilder.SubBuilderMap<?, ?, ?> map : maps) {
                handleSubBuilderMap(document.document(), map);
            }
        }
    }

    private static void handleSubBuilderMap(ClassDecl decl, SubBuilder.SubBuilderMap<?, ?, ?> map) {
        final List<String> comments = removeMethodAndGetComments(decl, map.methodName());
        for (var entry : map) {
            final MethodBuilder builder = new MethodBuilder(map.methodName())
                    .returnType(Types.THIS);
            class Builder implements SubBuilder.ProbeMethodBuilder {
                @Override
                public void param(String paramName, TypeInfo paramType) {
                    builder.param(paramName, CONST_STRING_CONVERTER.convertType(paramType));
                }
                @Override
                public String id() { return entry.getKey().toString(); }
                @Override
                public TypeInfo type() { return entry.getValue().type(); }
            }
            map.methodBuilder().accept(new Builder());
            final MethodDecl method = builder.build();
            method.addComments(comments);
            decl.members.add(method);
        }
    }

    private static Type literal(String str) {
        return Types.literal(str);
    }

    private static Type obj(Consumer<ObjectType.Builder> builder) {
        return Types.object(builder);
    }

    private static void singleObj(AliasRegistrar r, ClassPath clazz, String name, Type type) {
        r.addInputAlias(clazz, obj(b -> b.param(name, type)));
    }

    private static void obj(AliasRegistrar r, ClassPath clazz, String name, Consumer<ObjectType.Builder> builder) {
        singleObj(r, clazz, name, obj(builder));
    }

    private static void anonObj(AliasRegistrar r, ClassPath clazz, String name) {
        singleObj(r, clazz, name, Types.ANY);
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
            for (MethodDecl method : methods) {
                imports.addAll(method.getImports());
            }
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

        @Override
        public void setResolvedSymbols(Map<ClassPath, String> resolvedSymbols) {
            super.setResolvedSymbols(resolvedSymbols);
            for (MethodDecl method : methods) {
                method.setResolvedSymbols(resolvedSymbols);
            }
        }
    }
}
