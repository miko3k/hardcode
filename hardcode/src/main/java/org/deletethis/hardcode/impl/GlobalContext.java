package org.deletethis.hardcode.impl;

import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

class GlobalContext {
    private final Integer maxClassLines;
    private final String mainClassName;
    private final NumberNameAllocator classNameAllocator;
    private final ClassContext mainClassContext;
    private final List<ClassContext> classContexts = new ArrayList<>();

    GlobalContext(Integer maxClassLines, String mainClassName) {
        this.maxClassLines = maxClassLines;
        this.mainClassName = mainClassName;
        this.classNameAllocator = new NumberNameAllocator();
        this.classNameAllocator.newName(mainClassName);
        this.mainClassContext = new ClassContext(this, mainClassName, false);
        this.classContexts.add(mainClassContext);
    }

    ClassContext getMainClassContext() {
        return mainClassContext;
    }

    ClassContext getCurrentClassContext() {
        return classContexts.get(classContexts.size()-1);
    }

    ClassContext createAuxiliaryContext() {
        ClassContext cc = new ClassContext(this, classNameAllocator.newName(mainClassName), true);
        classContexts.add(cc);
        return cc;
    }

    Integer getMaxClassLines() { return maxClassLines; }

    List<TypeSpec> buildAll() {
        List<TypeSpec> result = new ArrayList<>(classContexts.size());
        for(ClassContext ctx: classContexts) {
            result.add(ctx.getTypeBuilder().build());
        }
        return result;
    }
}
