package org.deletethis.hardcode.impl;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import org.deletethis.hardcode.CrossRootReferenceException;
import org.deletethis.hardcode.graph.Digraph;
import org.deletethis.hardcode.objects.Expression;
import org.deletethis.hardcode.objects.ObjectContext;
import org.deletethis.hardcode.objects.ParameterName;

import javax.lang.model.element.Modifier;
import java.util.*;
import java.util.function.Supplier;

public class Printer {
    private final Digraph<ObjectInfo, ParameterName> graph;
    private final Map<ObjectInfo, ExprInfo> exprMap = new HashMap<>();
    private final NumberNameAllocator methodNameAllocator = new NumberNameAllocator();
    private final TypeSpec.Builder clz;
    private static final String METHOD_NAME = "get";

    private Expression printToContext(Context context, ObjectInfo n) {
        List<Expression> args = new ArrayList<>();

        for (ObjectInfo a : graph.getSuccessors(n)) {
            ExprInfo argument = print(context, a);

            if (context.getRoot() != argument.getRoot()) {
                throw new CrossRootReferenceException("cross-root reference");
            }

            args.add(argument.getExpression());
        }
        ObjectContextImpl objectContext = new ObjectContextImpl(args, n.getSplit());
        Expression code = n.getCode(context, objectContext);
        objectContext.verify();
        return code;
    }

    private ExprInfo print(Context context, ObjectInfo n) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(n);

        ExprInfo exprInfo = exprMap.get(n);
        if(exprInfo != null) {
            return exprInfo;
        }

        Expression expression;
        if(n.isRoot()) {
            Context child = createSubContext(n);
            Expression e = printToContext(child, n);
            child.finish(e);

            expression = Expression.complex("$L()", child.getMethodName());

        } else {
            expression = printToContext(context, n);
        }

        if (!expression.isSimple() && graph.getInDegree(n) > 1) {
            String var = context.allocateVariable(n.getType());
            context.addStatement("$T $L = $L", n.getType(), var, expression.getCode());
            expression = Expression.simple(var);
        }

        // root is printend in special way, and it should never end up in the map for itself
//        if(context.getRoot() == n)
//            throw new IllegalArgumentException();

        ExprInfo result = new ExprInfo(context.getRoot(), expression);
        exprMap.put(n, result);
        return result;
    }


    private Context createMainContext(Class<?> returnType, boolean supplier) {
        String name = methodNameAllocator.newName(METHOD_NAME);
        if(!name.equals(METHOD_NAME)) {
            throw new IllegalArgumentException();
        }

        Context context = new Context(methodNameAllocator, clz, graph.getRoot(), METHOD_NAME);
        MethodSpec.Builder mb = context.getMethodBuilder();
        mb.returns(returnType);
        mb.addModifiers(Modifier.PUBLIC);
        if(supplier) {
            mb.addAnnotation(Override.class);
        }
        return context;
    }

    private Context createSubContext(ObjectInfo root) {
        Class<?> returnType = root.getType();

        String nameHint = "create" + returnType.getSimpleName();

        Context context = new Context(methodNameAllocator, clz, root, methodNameAllocator.newName(nameHint));
        context.getMethodBuilder().returns(returnType);
        context.getMethodBuilder().addModifiers(Modifier.PRIVATE);

        return context;
    }


    public TypeSpec run(boolean supplier) {
        Class<?> returnType = graph.getRoot().getType();
        if(returnType == null) {
            returnType = Object.class;
        }
        if(supplier) {
            clz.addSuperinterface(ParameterizedTypeName.get(Supplier.class, returnType));
        }

        Context mainContext = createMainContext(returnType, supplier);
        ExprInfo mainMethod = print(mainContext, graph.getRoot());
        mainContext.finish(mainMethod.getExpression());
        return clz.build();
    }

    public Printer(String className, Digraph<ObjectInfo, ParameterName> graph) {
        TypeSpec.Builder result = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC);



        this.graph = graph;
        this.clz = result;

    }
}
