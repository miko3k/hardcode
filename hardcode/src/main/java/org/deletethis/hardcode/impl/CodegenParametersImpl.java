package org.deletethis.hardcode.impl;

import org.deletethis.hardcode.objects.Expression;
import org.deletethis.hardcode.objects.CodegenParameters;

import java.util.List;

class CodegenParametersImpl implements CodegenParameters {
    private List<Expression> arguments;
    private Integer split;
    //private boolean splitRequested;

    public CodegenParametersImpl(List<Expression> arguments, Integer split) {
        this.arguments = arguments;
        this.split = split;
    }

    public Integer getSplit() {
        //splitRequested = true;
        return split;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    void verify() {
/*        if(!splitRequested) {
            throw new IllegalStateException("code generator did not request value of split");
        }*/
    }
}
