package io.github.notenoughmail.worldjs;

import io.github.notenoughmail.worldjs.PlacedFeatureModifierEvent.ModifierFunction;
import io.github.notenoughmail.worldjs.PlacedFeatureModifierEvent.ModifierFunctions;
import io.github.notenoughmail.worldjs.PlacedFeatureModifierEvent.ModifierNamespace;
import io.github.notenoughmail.worldjs.builders.base.PlacedFeatureBuilder;
import moe.wolfgirl.probejs.lang.java.clazz.ClassPath;
import moe.wolfgirl.probejs.lang.transpiler.TypeConverter;
import moe.wolfgirl.probejs.lang.typescript.ScriptDump;
import moe.wolfgirl.probejs.lang.typescript.TypeScriptFile;
import moe.wolfgirl.probejs.lang.typescript.code.member.ClassDecl;
import moe.wolfgirl.probejs.lang.typescript.code.member.MethodDecl;
import moe.wolfgirl.probejs.lang.typescript.code.ts.Statements;
import moe.wolfgirl.probejs.lang.typescript.code.type.TSClassType;
import moe.wolfgirl.probejs.lang.typescript.code.type.Types;
import moe.wolfgirl.probejs.plugin.ProbeJSPlugin;
import moe.wolfgirl.probejs.utils.NameUtils;

import java.util.Map;

public class WorldJSProbePlugin extends ProbeJSPlugin {

    private static final ClassPath MODIFIER_HOLDER = new ClassPath(PlacedFeatureBuilder.Modifiers.class);

    @Override
    public void modifyClasses(ScriptDump scriptDump, Map<ClassPath, TypeScriptFile> globalClasses) {

        final Map<String, ModifierNamespace> modifiers = PlacedFeatureBuilder.Modifiers.NAMESPACES.get();

        final ClassDecl.Builder modifiersBuilder = Statements.clazz(MODIFIER_HOLDER.getName());

        for (Map.Entry<String, ModifierNamespace> entry : modifiers.entrySet()) {
            final String name = entry.getKey();
            final ModifierNamespace namespace = entry.getValue();

            final ClassPath namespacePath = new ClassPath(MODIFIER_HOLDER.getClassPath() + "$" + NameUtils.getCapitalized(name));
            modifiersBuilder.field(name, new TSClassType(namespacePath));

            final TypeScriptFile file = new TypeScriptFile(namespacePath);
            file.addCode(buildNamespace(scriptDump.transpiler.typeConverter, namespacePath, namespace));
            globalClasses.put(namespacePath, file);
        }

        final TypeScriptFile file = new TypeScriptFile(MODIFIER_HOLDER);
        file.addCode(modifiersBuilder.build());
        globalClasses.put(MODIFIER_HOLDER, file);
    }

    private static ClassDecl buildNamespace(TypeConverter typeConverter, ClassPath namespacePath, ModifierNamespace namespace) {
        final ClassDecl.Builder builder = Statements.clazz(namespacePath.getName());

        for (Map.Entry<String, ModifierFunctions> functionsEntry : namespace.functions.entrySet()) {
            final String functionName = functionsEntry.getKey();
            final ModifierFunctions functions = functionsEntry.getValue();

            for (ModifierFunction func : functions.functions.values()) {
                final int argLength = func.args().length();
                if (argLength == 0) {
                    builder.method(functionName, m -> m.returnType(Types.THIS));
                } else {
                    builder.method(functionName, method -> {
                        method.returnType(Types.THIS);
                        for (int i = 0; i < argLength ; i++) {
                            final PlacedFeatureModifierEvent.Args.Arg arg = func.args().get(i);
                            method.param(arg.name(), typeConverter.convertType(arg.type()));
                        }
                    });
                }

                final MethodDecl methodDecl = builder.methods.getLast();
                if (!func.probeDesc().isEmpty()) {
                    methodDecl.addComment(func.probeDesc());
                }
                if (func.args().length() != 0) {
                    methodDecl.linebreak();
                    for (PlacedFeatureModifierEvent.Args.Arg arg : func.args().args()) {
                        if (!arg.desc().isEmpty()) {
                            methodDecl.addComment("@param %s - %s".formatted(arg.name(), arg.desc()));
                        }
                    }
                }
            }
        }
        return builder.build();
    }
}
