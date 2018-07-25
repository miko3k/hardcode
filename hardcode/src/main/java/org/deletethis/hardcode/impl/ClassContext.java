package org.deletethis.hardcode.impl;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

public class ClassContext {
    private GlobalContext globalContext;
    private NumberNameAllocator methodNameAllocator;
    private final String clzName;
    private final TypeSpec.Builder clzBuilder;
    private int lineCountGuess;
    private boolean auxiliary;

    public ClassContext(GlobalContext globalContext, String clzName, boolean auxiliary) {
        this.globalContext = globalContext;
        this.methodNameAllocator = new NumberNameAllocator();
        this.clzName = clzName;

        this.clzBuilder = TypeSpec.classBuilder(clzName);
        if(auxiliary) {
            // add private constructor to auxiliary classes as they will contain only static methods
            this.clzBuilder.addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build());
        } else {
            this.clzBuilder.addModifiers(Modifier.PUBLIC);
        }
        this.lineCountGuess = 0;
        this.auxiliary = auxiliary;
    }

    public String getClzName() {
        return clzName;
    }

    public void addMethod(MethodSpec methodSpec) {
        lineCountGuess += methodSpec.toString().chars().filter(x -> x == '\n').count()+1;
        clzBuilder.addMethod(methodSpec);
    }

    public String allocateMethodName(String nameHint) {
        return methodNameAllocator.newName(nameHint);
    }

    public TypeSpec.Builder getTypeBuilder() {
        return clzBuilder;
    }

    public boolean isFull() {
        if(globalContext.getMaxClassLines() == null) {
            return false;
        } else {
            return lineCountGuess > globalContext.getMaxClassLines();
        }
    }

    public GlobalContext getGlobalContext() {
        return globalContext;
    }

    public boolean isAuxiliary() {
        return auxiliary;
    }
}
