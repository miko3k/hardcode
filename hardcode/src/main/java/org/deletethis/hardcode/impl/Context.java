package org.deletethis.hardcode.impl;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import org.deletethis.hardcode.ObjectInfo;
import org.deletethis.hardcode.graph.Divertex;
import org.deletethis.hardcode.objects.CodegenContext;
import org.deletethis.hardcode.objects.Expression;

import java.util.Objects;

class Context implements CodegenContext {
    private final Divertex<ObjectInfo> currentRoot;
    private final NumberNameAllocator variableAllocator = new NumberNameAllocator();
    private final MethodSpec.Builder methodBuilder;
    private final String methodName;

    private static AnnotationSpec unchecked() {
        AnnotationSpec.Builder builder = AnnotationSpec.builder(SuppressWarnings.class);
        builder.addMember("value", "$S", "unchecked");
        return builder.build();
    }

    Context(Divertex<ObjectInfo> currentRoot, String name) {
        this.currentRoot = currentRoot;
        this.methodBuilder = MethodSpec.methodBuilder(name);
        this.methodBuilder.addAnnotation(unchecked());
        this.methodName = name;
    }

    public MethodSpec.Builder getMethodBuilder() {
        return methodBuilder;
    }

    @Override
    public void addStatement(String format, Object... args) {
        methodBuilder.addStatement(format, args);
    }


    public MethodSpec finish(Expression expression) {
        methodBuilder.addStatement("return $L", expression.getCode());
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

    public Divertex<ObjectInfo> getRoot() {
        return currentRoot;
    }

    public String getMethodName() {
        return methodName;
    }
}
