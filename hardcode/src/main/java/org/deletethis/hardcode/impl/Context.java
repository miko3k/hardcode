package org.deletethis.hardcode.impl;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.deletethis.hardcode.ObjectInfo;
import org.deletethis.hardcode.graph.Divertex;
import org.deletethis.hardcode.objects.CodegenContext;
import org.deletethis.hardcode.objects.Expression;

import javax.lang.model.element.Modifier;
import java.util.Objects;

class Context implements CodegenContext {
    private final Divertex<ObjectInfo> currentRoot;
    private final NumberNameAllocator variableAllocator = new NumberNameAllocator();
    private final MethodSpec.Builder methodBuilder;
    private final String methodName;
    private final TypeSpec.Builder clz;
    private final NumberNameAllocator methodNameAllocator;

    private static AnnotationSpec unchecked() {
        AnnotationSpec.Builder builder = AnnotationSpec.builder(SuppressWarnings.class);
        builder.addMember("value", "$S", "unchecked");
        return builder.build();
    }

    Context(NumberNameAllocator methodNameAllocator, TypeSpec.Builder clz, Divertex<ObjectInfo> currentRoot, String nameHint) {
        this.methodNameAllocator = methodNameAllocator;
        this.clz = clz;
        this.currentRoot = currentRoot;
        this.methodName = methodNameAllocator.newName(nameHint);
        this.methodBuilder = MethodSpec.methodBuilder(methodName);
        this.methodBuilder.addAnnotation(unchecked());
    }

    public MethodSpec.Builder getMethodBuilder() {
        return methodBuilder;
    }

    @Override
    public void addStatement(String format, Object... args) {
        methodBuilder.addStatement(format, args);
    }

    @Override
    public void finish() {
        clz.addMethod(methodBuilder.build());
    }


    public void finish(Expression expression) {
        methodBuilder.addStatement("return $L", expression.getCode());
        finish();
    }

    @Override
    public CodegenContext createVoidMethod(String nameHint, Class<?> paramType, String paramName) {
        Context context = new Context(methodNameAllocator, clz, currentRoot, methodNameAllocator.newName(nameHint));
        context.getMethodBuilder().returns(Void.TYPE);
        context.getMethodBuilder().addModifiers(Modifier.PRIVATE);
        context.getMethodBuilder().addParameter(paramType, paramName);
        return context;
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
