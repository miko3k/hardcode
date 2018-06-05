package org.deletethis.hardcode.impl;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.deletethis.hardcode.HardcodeException;
import org.deletethis.hardcode.ObjectInfo;
import org.deletethis.hardcode.graph.Digraph;
import org.deletethis.hardcode.graph.Divertex;
import org.deletethis.hardcode.objects.Expression;

import javax.lang.model.element.Modifier;
import java.util.*;

public class Printer {
    private final Digraph<ObjectInfo> graph;
    private final Map<Divertex<ObjectInfo>, ExprInfo> globalExprMap = new HashMap<>();


    private ExprInfo print(Context context, Divertex<ObjectInfo> n) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(n);

        ObjectInfo objectInfo = n.getPayload();
        ExprInfo exprInfo = context.getExprInfo(n);
        if(exprInfo != null) {
            return exprInfo;
        }

        Expression expression;
        if(objectInfo.isRoot() && context.getCurrentRoot() != n) {
            throw new IllegalStateException();
        } else {
            List<Expression> args = new ArrayList<>();

            for (Divertex<ObjectInfo> a : n.getSuccessors()) {
                ExprInfo argument = print(context, a);

                if (context != argument.getContext()) {
                    throw new HardcodeException("cross-root reference");
                }

                args.add(argument.getExpression());
            }
            expression = objectInfo.getCode(context, args);
        }

        if (!expression.isSimple() && n.getInDegree() > 1) {
            String var = context.allocateVariable(objectInfo.getType());
            context.addStatement("$T $L = $L", objectInfo.getType(), var, expression.getCode());
            expression = Expression.simple(var);
        }

        return context.putExprInfo(n, expression);
    }


    private Context createMainContext(String name, List<Root> roots) {
        Context context = new Context(null, globalExprMap, name);
        Class<?> returnType = graph.getRoot().getPayload().getType();
        MethodSpec.Builder mb = context.getMethodBuilder();
        if(returnType == null) {
            mb.returns(Object.class);
        } else {
            mb.returns(returnType);
        }
        mb.addModifiers(Modifier.PUBLIC);

        for(Root r: roots) {
            Class<?> varType = r.vertex.getPayload().getType();

            String varName = context.getVariableAllocator().newName(varType, r.vertex);
            mb.addCode("$[$T $L = $L(", varType, varName, r.methodName);

            boolean first = true;
            for(Divertex<ObjectInfo> p: r.dependencies) {
                if(first) {
                    first = false;
                } else {
                    mb.addCode(",");
                }
                mb.addCode("$L", context.getVariableAllocator().get(p));
            }

            mb.addCode(");\n$]");

            context.addLocalExpression(r.vertex, Expression.simple(varName));
        }
        mb.addCode("\n");
        return context;
    }

    private Context createSubContext(Root root) {
        Class<?> returnType = root.vertex.getPayload().getType();

        Context context = new Context(root.vertex, globalExprMap, root.methodName);
        context.getMethodBuilder().returns(returnType);
        context.getMethodBuilder().addModifiers(Modifier.PRIVATE);

        for(Divertex<ObjectInfo> dep: root.dependencies) {
            Class<?> type = dep.getPayload().getType();
            String variableName = context.allocateVariable(type);
            context.getMethodBuilder().addParameter(type, variableName);
            context.addLocalExpression(dep, Expression.simple(variableName));
        }

        return context;
    }


    public void run(TypeSpec.Builder clz, String nameHint) {
        NumberNameAllocator methodNameAllocator = new NumberNameAllocator();
        String methodName = methodNameAllocator.newName(nameHint);

        List<Root> roots = Roots.getRoots(methodNameAllocator, graph);

        Context mainContext = createMainContext(methodName, roots);
        ExprInfo print = print(mainContext, graph.getRoot());

        for(Root r: roots) {
            Context context = createSubContext(r);
            print = print(context, r.vertex);
            clz.addMethod(context.finish(print));
        }
        clz.addMethod(mainContext.finish(print));
    }

    public Printer(Digraph<ObjectInfo> graph) {
        this.graph = graph;
    }
}
