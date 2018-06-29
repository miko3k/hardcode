package org.deletethis.hardcode.impl;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.deletethis.hardcode.objects.CodegenContext;
import org.deletethis.hardcode.objects.Expression;

import javax.lang.model.element.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

class Context implements CodegenContext {
    private final ObjectInfo currentRoot;
    private final NumberNameAllocator variableAllocator = new NumberNameAllocator();
    private final MethodSpec.Builder methodBuilder;
    private final String methodName;
    private final TypeSpec.Builder clz;
    private final NumberNameAllocator methodNameAllocator;
    private final Set<Class<?>> unhandledExceptions = new HashSet<>();
    private final CodeBlock.Builder code = CodeBlock.builder();

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

    MethodSpec.Builder getMethodBuilder() {
        return methodBuilder;
    }

    @Override
    public void addStatement(String format, Object... args) {
        code.addStatement(format, args);
    }

    void finish(Expression expression) {
        String exceptionVariable = allocateVariable(Exception.class);

        if(expression != null) {
            code.addStatement("return $L", expression.getCode());
        }

        if(!unhandledExceptions.isEmpty()) {
            methodBuilder.beginControlFlow("try");
        }

        methodBuilder.addCode(code.build());

        if(!unhandledExceptions.isEmpty()) {
            // we are not using multi catch here - it would be more difficult and would make code require java 1.7
            for(Class<?> c: unhandledExceptions) {
                methodBuilder.nextControlFlow("catch($T $L)", c, exceptionVariable);
                methodBuilder.addStatement("throw new $T($L)", IllegalStateException.class, exceptionVariable);
            }
            methodBuilder.endControlFlow();
        }

        clz.addMethod(methodBuilder.build());
    }


    @Override
    public void finish() {
        finish(null);
    }


    @Override
    public CodegenContext createProcedure(String nameHint, String paramName, Class<?> paramType) {
        Context context = new Context(methodNameAllocator, clz, currentRoot, methodNameAllocator.newName(nameHint));
        if(!context.variableAllocator.newName(paramName).equals(paramName))
            throw new IllegalStateException("wtf?");
        context.getMethodBuilder().returns(Void.TYPE);
        context.getMethodBuilder().addModifiers(Modifier.PRIVATE);
        context.getMethodBuilder().addParameter(paramType, paramName);
        return context;
    }

    @Override
    public String allocateVariable(Class<?> hint) {
        Objects.requireNonNull(hint);
        return variableAllocator.newName(hint);
    }

    ObjectInfo getRoot() {
        return currentRoot;
    }

    public String getMethodName() {
        return methodName;
    }

    public void addUnhandled(Collection<Class<?>> exception) {
        unhandledExceptions.addAll(exception);
    }
}
