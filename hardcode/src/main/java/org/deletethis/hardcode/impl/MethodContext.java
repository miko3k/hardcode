package org.deletethis.hardcode.impl;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import org.deletethis.hardcode.objects.CodegenContext;
import org.deletethis.hardcode.objects.Expression;
import org.deletethis.hardcode.objects.ProcedureContext;

import javax.lang.model.element.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class MethodContext implements CodegenContext, ProcedureContext {
    private final ClassContext classContext;
    private final NumberNameAllocator variableAllocator = new NumberNameAllocator();
    private final MethodSpec.Builder methodBuilder;
    private final String methodName;
    private final Set<Class<? extends Throwable>> unhandledExceptions = new HashSet<>();
    private final CodeBlock.Builder code = CodeBlock.builder();

    MethodContext(ClassContext classContext, String name) {
        this.classContext = classContext;
        this.methodName = name;
        this.methodBuilder = MethodSpec.methodBuilder(methodName);
        this.methodBuilder.addAnnotation(unchecked());
    }

    private static AnnotationSpec unchecked() {
        AnnotationSpec.Builder builder = AnnotationSpec.builder(SuppressWarnings.class);
        builder.addMember("value", "$S", "unchecked");
        return builder.build();
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

        MethodSpec methodSpec = methodBuilder.build();

        classContext.addMethod(methodSpec);
    }


    @Override
    public void finish() {
        finish(null);
    }

    @Override
    public ProcedureContext createProcedure(String nameHint, String paramName, Class<?> paramType) {
        ClassContext myClassContext;
        if(classContext.isFull()) {
            myClassContext = classContext.getGlobalContext().createAuxiliaryContext();
        } else {
            myClassContext = classContext;
        }

        MethodContext context = new MethodContext(myClassContext, myClassContext.allocateMethodName(nameHint));

        if(!context.variableAllocator.newName(paramName).equals(paramName)) {
            throw new IllegalStateException("wtf?");
        }
        context.getMethodBuilder().returns(Void.TYPE);
        context.getMethodBuilder().addModifiers(Modifier.STATIC);
        context.getMethodBuilder().addParameter(paramType, paramName);
        return context;
    }

    @Override
    public String allocateVariable(Class<?> hint) {
        Objects.requireNonNull(hint);
        return variableAllocator.newName(hint);
    }

    @Override
    public String getClassName() {
        return classContext.getClzName();
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    void addFatalExceptions(Collection<Class<? extends Throwable>> exception) {
        unhandledExceptions.addAll(exception);
    }


}
