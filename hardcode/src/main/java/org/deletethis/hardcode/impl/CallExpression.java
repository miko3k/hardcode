package org.deletethis.hardcode.impl;

import com.squareup.javapoet.CodeBlock;
import org.deletethis.hardcode.objects.Expression;

public class CallExpression implements Expression {
    private MethodContext targetMethod;
    private String parameter;

    public CallExpression(MethodContext targetMethod, String parameter) {
        this.targetMethod = targetMethod;
        this.parameter = parameter;
    }

    public CallExpression(MethodContext targetMethod) {
        this(targetMethod, null);
    }

    @Override
    public CodeBlock getCode(String className) {
        String methodName = targetMethod.getMethodName();
        String targetClassName = targetMethod.getClassName();

        if(className.equals(targetClassName)) {
            if(parameter != null) {
                assert targetMethod.getParameters().size() == 1;
                return CodeBlock.of("$L($L)", methodName, parameter);
            } else {
                assert targetMethod.getParameters().isEmpty();
                return CodeBlock.of("$L()", methodName);
            }
        } else {
            if(targetMethod.getAccessModifier() == AccessModifier.PRIVATE)
                targetMethod.setAccessModifier(AccessModifier.PACKAGE);

            if(parameter != null) {
                assert targetMethod.getParameters().size() == 1;
                return CodeBlock.of("$L.$L($L)", targetClassName, methodName, parameter);
            } else {
                assert targetMethod.getParameters().isEmpty();
                return CodeBlock.of("$L.$L()", targetClassName, methodName);
            }
        }
    }

    @Override
    public boolean isSimple() {
        return false;
    }

}
