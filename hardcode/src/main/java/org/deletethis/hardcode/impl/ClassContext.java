package org.deletethis.hardcode.impl;

import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

class ClassContext {
    private GlobalContext globalContext;
    private NumberNameAllocator methodNameAllocator;
    private final String clzName;
    private int lineCountGuess;
    private List<MethodContext> methods = new ArrayList<>();


    ClassContext(GlobalContext globalContext, String clzName) {
        this.globalContext = globalContext;
        this.methodNameAllocator = new NumberNameAllocator();
        this.clzName = clzName;
        this.lineCountGuess = 0;
    }

    String getClassName() {
        return clzName;
    }

    void addMethod(MethodContext methodContext) {
        lineCountGuess += methodContext.getLineCount();
        methods.add(methodContext);
    }

    String allocateMethodName(String nameHint) {
        return methodNameAllocator.newName(nameHint);
    }

    boolean isFull() {
        if(globalContext.getMaxClassLines() == null) {
            return false;
        } else {
            return lineCountGuess > globalContext.getMaxClassLines();
        }
    }

    GlobalContext getGlobalContext() {
        return globalContext;
    }

    List<MethodContext> getMethods() {
        return methods;
    }
}
