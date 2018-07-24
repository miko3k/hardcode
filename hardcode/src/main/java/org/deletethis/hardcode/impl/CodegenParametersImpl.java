package org.deletethis.hardcode.impl;

import org.deletethis.hardcode.objects.Expression;
import org.deletethis.hardcode.objects.CodegenParameters;
import org.deletethis.hardcode.objects.ParameterName;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class CodegenParametersImpl implements CodegenParameters {
    static class CodegenArgument implements Argument {
        private final Expression expression;
        private final ParameterName parameterName;

        public CodegenArgument(Expression expression, ParameterName parameterName) {
            this.expression = Objects.requireNonNull(expression);
            this.parameterName = Objects.requireNonNull(parameterName);
        }

        @Override
        public Expression getExpression() {
            return expression;
        }

        @Override
        public ParameterName getName() {
            return parameterName;
        }
    }

    private List<Argument> arguments;
    private Integer split;
    private List<Expression> expressions;
    //private boolean splitRequested;

    CodegenParametersImpl(List<Argument> arguments, Integer split) {
        this.arguments = arguments;
        this.split = split;
        this.expressions = arguments.stream().map(Argument::getExpression).collect(Collectors.toList());
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
