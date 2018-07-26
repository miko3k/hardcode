package org.deletethis.hardcode.impl;

import com.squareup.javapoet.CodeBlock;
import org.deletethis.hardcode.objects.Expression;
import org.deletethis.hardcode.objects.CodegenParameters;
import org.deletethis.hardcode.objects.ParameterName;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class CodegenParametersImpl implements CodegenParameters {
    static class ArgumentImpl implements Argument {
        private final Expression expression;
        private final ParameterName parameterName;

        ArgumentImpl(Expression expression, ParameterName parameterName) {
            this.expression = Objects.requireNonNull(expression);
            this.parameterName = Objects.requireNonNull(parameterName);
        }

        @Override
        public boolean isSimple() {
            return expression.isSimple();
        }

        @Override
        public ParameterName getName() {
            return parameterName;
        }

        @Override
        public CodeBlock getCode(String className) {
            return expression.getCode(className);
        }
    }

    private List<Argument> arguments;
    private Integer split;
    //private boolean splitRequested;

    CodegenParametersImpl(List<Argument> arguments, Integer split) {
        this.arguments = arguments;
        this.split = split;
    }

    @Override
    public Integer getSplit() {
        //splitRequested = true;
        return split;
    }

    @Override
    public List<Argument> getArgumentList() {
        return arguments;
    }

    void verify() {
/*        if(!splitRequested) {
            throw new IllegalStateException("code generator did not request value of split");
        }*/
    }
}
