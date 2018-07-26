package org.deletethis.hardcode.impl;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import org.deletethis.hardcode.objects.CodegenContext;
import org.deletethis.hardcode.objects.Expression;
import org.deletethis.hardcode.objects.ProcedureContext;

import javax.lang.model.element.Modifier;
import java.util.*;

public class MethodContext implements CodegenContext, ProcedureContext {
    private final ClassContext classContext;
    private final NumberNameAllocator variableAllocator = new NumberNameAllocator();
    private final String methodName;
    private final List<ParameterSpec> parameters = new ArrayList<>();
    private AccessModifier accessModifier = AccessModifier.PRIVATE;
    private final Set<Class<? extends Throwable>> unhandledExceptions = new HashSet<>();
    private final Class<?> returnType;
    private final CodeBlock.Builder code = CodeBlock.builder();


    MethodContext(ClassContext classContext, String name, Class<?> returnType, ParameterSpec... parameters) {
        this.classContext = classContext;
        this.methodName = name;
        this.returnType = returnType;
        this.parameters.addAll(Arrays.asList(parameters));
    }

    @Override
    public void addStatement(String format, Object... args) {
        code.addStatement(format, args);
    }

    void finish(Expression expression) {

        if(expression != null) {
            code.addStatement("return $L", expression.getCode(classContext.getClassName()));
        }

        classContext.addMethod(this);
    }


    @Override
    public void finish() {
        finish(null);
    }

    @Override
    public ProcedureContext createProcedure(String nameHint, String paramName, Class<?> paramType) {
        ClassContext cc = classContext.getGlobalContext().getCurrentClassContext();

        ClassContext myClassContext;
        if(cc.isFull()) {
            myClassContext = cc.getGlobalContext().createAuxiliaryContext();
        } else {
            myClassContext = cc;
        }

        MethodContext context = new MethodContext(
                myClassContext,
                myClassContext.allocateMethodName(nameHint),
                Void.TYPE,
                ParameterSpec.builder(paramType, paramName).build());

        if(!context.variableAllocator.newName(paramName).equals(paramName)) {
            throw new IllegalStateException("wtf?");
        }
        return context;
    }

    AccessModifier getAccessModifier() {
        return accessModifier;
    }


    int getLineCount() {
        int cnt = (int)code.build().toString().chars().filter(x -> x == '\n').count();
        cnt += 1;
        if(!unhandledExceptions.isEmpty()) {
            cnt += 4;
        }
        return cnt;
    }


    @Override
    public String allocateVariable(Class<?> hint) {
        Objects.requireNonNull(hint);
        return variableAllocator.newName(hint);
    }

    @Override
    public String getClassName() {
        return classContext.getClassName();
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    void addFatalExceptions(Collection<Class<? extends Throwable>> exception) {
        unhandledExceptions.addAll(exception);
    }

    @Override
    public Expression getCallExpression(String paramValue) {
        assert parameters.size() == 1;

        return new Expression() {
            @Override
            public CodeBlock getCode(String className) {
                if(className.equals(getClassName())) {
                    return CodeBlock.of("$L($L)", methodName, paramValue);
                } else {
                    if(accessModifier == AccessModifier.PRIVATE)
                        accessModifier = AccessModifier.PACKAGE;

                    return CodeBlock.of("$L.$L($L)", getClassName(), methodName, paramValue);
                }
            }

            @Override
            public boolean isSimple() {
                return false;
            }
        };
    }

    Set<Class<? extends Throwable>> getUnhandledExceptions() {
        return unhandledExceptions;
    }

    Class<?> getReturnType() {
        return returnType;
    }

    CodeBlock.Builder getCode() {
        return code;
    }

    List<ParameterSpec> getParameters() {
        return parameters;
    }
}
