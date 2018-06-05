package org.deletethis.hardcode.impl;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import org.deletethis.hardcode.HardcodeException;
import org.deletethis.hardcode.ObjectInfo;
import org.deletethis.hardcode.graph.Divertex;
import org.deletethis.hardcode.objects.CodegenContext;
import org.deletethis.hardcode.objects.Expression;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class Context implements CodegenContext {
    private final Divertex<ObjectInfo> currentRoot;
    private final NumberNameAllocator variableAllocator = new NumberNameAllocator();
    private final MethodSpec.Builder methodBuilder;
    private final Map<Divertex<ObjectInfo>, ExprInfo> globalExprMap;
    private final Map<Divertex<ObjectInfo>, ExprInfo> localExprMap = new HashMap<>();

    private static AnnotationSpec unchecked() {
        AnnotationSpec.Builder builder = AnnotationSpec.builder(SuppressWarnings.class);
        builder.addMember("value", "$S", "unchecked");
        return builder.build();
    }

    Context(Divertex<ObjectInfo> currentRoot, Map<Divertex<ObjectInfo>, ExprInfo> globalExprMap, String name) {
        this.currentRoot = currentRoot;
        this.globalExprMap = globalExprMap;
        this.methodBuilder = MethodSpec.methodBuilder(name);
        this.methodBuilder.addAnnotation(unchecked());
    }

    public MethodSpec.Builder getMethodBuilder() {
        return methodBuilder;
    }

    @Override
    public void addStatement(String format, Object... args) {
        methodBuilder.addStatement(format, args);
    }


    public MethodSpec finish(ExprInfo expression) {
        if(expression.getContext() != this) {
            throw new HardcodeException("cross-root reference");
        }
        methodBuilder.addStatement("return $L", expression.getExpression().getCode());
        return methodBuilder.build();
    }

    public NumberNameAllocator getVariableAllocator() {
        return variableAllocator;
    }

    @Override
    public String allocateVariable(Class<?> hint) {
        Objects.requireNonNull(hint);
        return variableAllocator.newName(hint);
    }

    private Map<Divertex<ObjectInfo>, ExprInfo> scope(Divertex<ObjectInfo> vertex) {
        if(vertex.getPayload().isRoot()) {
            return localExprMap;
        } else {
            return globalExprMap;
        }
    }

    void addLocalExpression(Divertex<ObjectInfo> v, Expression expression) {
        localExprMap.put(v, new ExprInfo(this, expression));
    }

    ExprInfo getExprInfo(Divertex<ObjectInfo> vertex) {
        return scope(vertex).get(vertex);
    }

    ExprInfo putExprInfo(Divertex<ObjectInfo> vertex, Expression expression) {
        if(vertex.getPayload().isRoot() && vertex != currentRoot)
            throw new IllegalArgumentException("vertex: " + vertex + ", expression: " + expression);

        Map<Divertex<ObjectInfo>, ExprInfo> scope = scope(vertex);

        ExprInfo result = new ExprInfo(this, expression);

        ExprInfo exprInfo = scope.get(vertex);
        if(exprInfo != null) {
            throw new IllegalArgumentException("already exists: " + vertex + " (previous: " + exprInfo + ", current: " + result);
        }

        scope.put(vertex, result);
        return result;
    }

    public Divertex<ObjectInfo> getCurrentRoot() {
        return currentRoot;
    }
}
