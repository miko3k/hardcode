package org.deletethis.hardcode.impl;

import org.deletethis.hardcode.objects.Expression;
import org.deletethis.hardcode.objects.ObjectContext;

import java.util.Iterator;
import java.util.List;

public class ObjectContextImpl implements ObjectContext {
    private List<Expression> arguments;
    private Integer split;
    //private boolean splitRequested;

    public ObjectContextImpl(List<Expression> arguments, Integer split) {
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
