package org.deletethis.hardcode.impl;

import com.squareup.javapoet.*;
import org.deletethis.hardcode.graph.Digraph;
import org.deletethis.hardcode.graph.Divertex;
import org.deletethis.hardcode.HardcodeException;
import org.deletethis.hardcode.ObjectInfo;

import org.deletethis.hardcode.objects.CodegenContext;
import org.deletethis.hardcode.objects.Expression;

import javax.lang.model.element.Modifier;
import java.beans.Introspector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Printer {
    private final Map<Divertex, ExprInfo> exprMap = new HashMap<>();
    private final TypeSpec.Builder clz;
    private final NumberNameAllocator methodAllocator = new NumberNameAllocator();

    private Printer(TypeSpec.Builder clz) {
        this.clz = clz;
    }

    private class Context implements CodegenContext {
        private final MethodSpec.Builder methodBuilder;
        // sadly, there's no getter in builder, we need to store it separately
        private final String name;
        private final NumberNameAllocator variableAllocator = new NumberNameAllocator();


        Context(String nameHint, Class<?> type, Modifier modifier) {
            this.name = methodAllocator.newName(nameHint);
            this.methodBuilder = MethodSpec.methodBuilder(name);
            AnnotationSpec.Builder builder = AnnotationSpec.builder(SuppressWarnings.class);
            builder.addMember("value", "$S", "unchecked");
            this.methodBuilder.addAnnotation(builder.build());
            if(type == null) {
                // this should happen only on top level as void is not a root type
                methodBuilder.returns(Object.class);
            } else {
                methodBuilder.returns(type);
            }
            methodBuilder.addModifiers(modifier);
        }

        @Override
        public String allocateVariable(Class<?> hint) {
            return variableAllocator.newName(Introspector.decapitalize(hint.getSimpleName()));
        }


        public void addStatement(String format, Object... args) {
            methodBuilder.addStatement(format, args);
        }

        String getMethodName() {
            return name;
        }

        void finish(Expression expression) {
            methodBuilder.addStatement("return $L", expression.getCode());
            clz.addMethod(methodBuilder.build());
        }
    }

    

    private Expression printToContext(Context context, Divertex<ObjectInfo> n) {
        ObjectInfo objectInfo = n.getPayload();

        List<Expression> args = new ArrayList<>();

        for(Divertex<ObjectInfo> a: n.getSuccessors()) {
            ExprInfo exprInfo = print(context, a);
            if(exprInfo.getContext() != context) {
                throw new HardcodeException("cross-root reference");
            }
            args.add(exprInfo.getExpression());
        }

        return objectInfo.getCode(context, args);
    }

    private ExprInfo print(Context context, Divertex<ObjectInfo> n) {
        ExprInfo exprInfo = exprMap.get(n);
        if(exprInfo != null) {
            return exprInfo;
        }

        ObjectInfo objectInfo = n.getPayload();
        Expression expression;
        if(objectInfo.isRoot()) {
            Class<?> type = objectInfo.getType();
            if(type == null) {
                // should not happen
                throw new IllegalStateException();
            }

            Context methodContext = new Context("create" + type.getSimpleName(), type, Modifier.PRIVATE);
            Expression methodValue = printToContext(methodContext, n);
            methodContext.finish(methodValue);

            expression = Expression.complex("$L()", methodContext.getMethodName());
        } else {
            expression = printToContext(context, n);
        }

        if(n.getInDegree() > 1 && !expression.isSimple()) {
            String var = context.allocateVariable(objectInfo.getType());
            context.addStatement("$T $L = $L", objectInfo.getType(), var, expression.getCode());
            expression = Expression.simple(var);
        }
        exprInfo = new ExprInfo(expression, context);
        exprMap.put(n, exprInfo);
        return exprInfo;
    }

    private void print(Digraph<ObjectInfo> graph, String methodName) {
        Context context = new Context(methodName, graph.getRoot().getPayload().getType(), Modifier.PUBLIC);
        Expression expression = print(context, graph.getRoot()).getExpression();
        context.finish(expression);
    }

    public static void print(TypeSpec.Builder clz, String methodName, Digraph<ObjectInfo> graph) {
        Printer p = new Printer(clz);
        p.print(graph, methodName);
    }
}
