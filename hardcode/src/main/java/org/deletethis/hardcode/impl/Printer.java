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
    private final Map<Divertex<ObjectInfo>, ExprInfo> exprMap = new HashMap<>();
    private final NumberNameAllocator methodNameAllocator = new NumberNameAllocator();
    private final TypeSpec.Builder clz;

    private Expression printToContext(Context context, Divertex<ObjectInfo> n) {
        ObjectInfo objectInfo = n.getPayload();
        List<Expression> args = new ArrayList<>();

        for (Divertex<ObjectInfo> a : n.getSuccessors()) {
            ExprInfo argument = print(context, a);

            if (context.getRoot() != argument.getRoot()) {
                throw new HardcodeException("cross-root reference");
            }

            args.add(argument.getExpression());
        }
        return objectInfo.getCode(context, args);
    }

    private ExprInfo print(Context context, Divertex<ObjectInfo> n) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(n);

        ObjectInfo objectInfo = n.getPayload();
        ExprInfo exprInfo = exprMap.get(n);
        if(exprInfo != null) {
            return exprInfo;
        }

        Expression expression;
        if(n.getPayload().isRoot()) {
            Context child = createSubContext(n);
            Expression e = printToContext(child, n);
            MethodSpec finish = child.finish(e);
            clz.addMethod(finish);

            expression = Expression.complex("$L()", child.getMethodName());

        } else {
            expression = printToContext(context, n);
        }

        if (!expression.isSimple() && n.getInDegree() > 1) {
            String var = context.allocateVariable(objectInfo.getType());
            context.addStatement("$T $L = $L", objectInfo.getType(), var, expression.getCode());
            expression = Expression.simple(var);
        }

        // root is printend in special way, and it should never end up in the map for itself
//        if(context.getRoot() == n)
//            throw new IllegalArgumentException();

        ExprInfo result = new ExprInfo(context.getRoot(), expression);
        exprMap.put(n, result);
        return result;
    }


    private Context createMainContext(String nameHint) {
        Context context = new Context(graph.getRoot(), methodNameAllocator.newName(nameHint));
        Class<?> returnType = graph.getRoot().getPayload().getType();
        MethodSpec.Builder mb = context.getMethodBuilder();
        if(returnType == null) {
            mb.returns(Object.class);
        } else {
            mb.returns(returnType);
        }
        mb.addModifiers(Modifier.PUBLIC);
        return context;
    }

    private Context createSubContext(Divertex<ObjectInfo> root) {
        Class<?> returnType = root.getPayload().getType();

        String nameHint = "create" + returnType.getSimpleName();

        Context context = new Context(root, methodNameAllocator.newName(nameHint));
        context.getMethodBuilder().returns(returnType);
        context.getMethodBuilder().addModifiers(Modifier.PRIVATE);

        return context;
    }


    public void run(String nameHint) {

        Context mainContext = createMainContext(nameHint);
        ExprInfo mainMethod = print(mainContext, graph.getRoot());
        clz.addMethod(mainContext.finish(mainMethod.getExpression()));
    }

    public Printer(TypeSpec.Builder clz, Digraph<ObjectInfo> graph) {
        this.graph = graph;
        this.clz = clz;
    }
}
