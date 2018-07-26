package org.deletethis.hardcode.impl;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import org.deletethis.hardcode.graph.ConnectedVertex;
import org.deletethis.hardcode.graph.Digraph;
import org.deletethis.hardcode.objects.CodegenParameters;
import org.deletethis.hardcode.objects.Expression;
import org.deletethis.hardcode.objects.ParameterName;

import javax.lang.model.element.Modifier;
import java.util.*;
import java.util.function.Supplier;

class Printer {
    private final Digraph<ObjectInfo, ParameterName> graph;
    private final Map<ObjectInfo, Expression> exprMap = new HashMap<>();
    private final GlobalContext globalContext;
    private static final String METHOD_NAME = "get";

    private Expression printToContext(MethodContext context, ObjectInfo n) {
        List<CodegenParameters.Argument> args = new ArrayList<>();

        for (ConnectedVertex<ObjectInfo, ParameterName> conn : graph.getSuccessorConnections(n)) {
            Expression argument = print(context, conn.getVertex());
            args.add(new CodegenParametersImpl.ArgumentImpl(argument, conn.getEdge()));
        }
        context.addFatalExceptions(n.getFatalExceptions());
        CodegenParametersImpl params = new CodegenParametersImpl(args, n.getSplit());
        Expression code = n.getCodeGenerator().getCode(context, params);
        params.verify();
        return code;
    }

    private Expression print(MethodContext context, ObjectInfo n) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(n);

        Expression exprInfo = exprMap.get(n);
        if(exprInfo != null) {
            return exprInfo;
        }

        Expression expression;
        if(n.isRoot()) {
            MethodContext child = createSubContext(n);

            Expression e = printToContext(child, n);
            child.finish(e);

            expression = Expression.complex("$L.$L()", child.getClassName(), child.getMethodName());

        } else {
            expression = printToContext(context, n);
        }

        if (!expression.isSimple() && graph.getInDegree(n) > 1) {
            String var = context.allocateVariable(n.getType());
            context.addStatement("$T $L = $L", n.getType(), var, expression.getCode(context.getClassName()));
            expression = Expression.simple(var);
        }

        exprMap.put(n, expression);
        return expression;
    }

    private MethodContext createMainContext(Class<?> returnType) {
        String name = globalContext.getMainClassContext().allocateMethodName(METHOD_NAME);
        if(!name.equals(METHOD_NAME)) {
            throw new IllegalArgumentException();
        }

        MethodContext context = new MethodContext(globalContext.getMainClassContext(), METHOD_NAME, returnType);
        return context;
    }

    private MethodContext createSubContext(ObjectInfo root) {
        Class<?> returnType = root.getType();

        String nameHint = "create" + returnType.getSimpleName();

        ClassContext classContext = globalContext.getCurrentClassContext();
        if(classContext.isFull()) {
            classContext = globalContext.createAuxiliaryContext();
        }

        return new MethodContext(classContext, classContext.allocateMethodName(nameHint), returnType);
    }


    List<TypeSpec> run(boolean supplier) {

        Class<?> returnType = graph.getRoot().getType();
        if(returnType == null) {
            returnType = Object.class;
        }

        MethodContext mainContext = createMainContext(returnType);
        Expression mainMethod = print(mainContext, graph.getRoot());
        mainContext.finish(mainMethod);


        return globalContext.buildAll(mainContext, supplier);
    }

    Printer(String className, Digraph<ObjectInfo, ParameterName> graph, Integer maxLines) {
        globalContext = new GlobalContext(maxLines, className);

        this.graph = graph;
    }
}
