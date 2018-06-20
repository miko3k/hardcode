package org.deletethis.hardcode.impl;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.deletethis.hardcode.objects.CodegenContext;
import org.deletethis.hardcode.objects.Expression;
import org.deletethis.hardcode.objects.ObjectInfo;

import javax.lang.model.element.Modifier;
import java.util.Objects;

class Context implements CodegenContext {
    private final ObjectInfo currentRoot;
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

    Context(NumberNameAllocator methodNameAllocator, TypeSpec.Builder clz, ObjectInfo currentRoot, String name) {
        this.methodNameAllocator = methodNameAllocator;
        this.clz = clz;
        this.currentRoot = currentRoot;
        this.methodName = name;
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
        if(!context.variableAllocator.newName(paramName).equals(paramName))
            throw new IllegalStateException("wtf?");
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

    public ObjectInfo getRoot() {
        return currentRoot;
    }

    public String getMethodName() {
        return methodName;
    }
}
