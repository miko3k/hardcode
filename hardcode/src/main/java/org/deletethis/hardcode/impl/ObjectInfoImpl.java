package org.deletethis.hardcode.impl;

import org.deletethis.hardcode.ConfigMismatchException;
import org.deletethis.hardcode.objects.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

class ObjectInfoImpl implements ObjectInfo {
    private final Class<?> type;
    private final CodeGenerator codeGenerator;
    private boolean root;
    private Integer split;
    private final String asString;
    private Collection<Class<? extends Throwable>> fatalExceptions;

    public ObjectInfoImpl(NodeDefinition def) {
        Collection<Class<? extends Throwable>> fatalExceptions = def.getFatalExceptions();
        if(fatalExceptions == null)
            fatalExceptions = Collections.emptyList();

        this.type = Objects.requireNonNull(def.getType());
        this.codeGenerator = Objects.requireNonNull(def.getConstructionStrategy());
        this.root = false;
        this.split = null;
        this.asString = Objects.requireNonNull(def.toString());
        this.fatalExceptions = fatalExceptions;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public CodeGenerator getCodeGenerator() {
        return codeGenerator;
    }

    @Override
    public boolean isRoot() {
        return root;
    }

    @Override
    public String toString() {
        return asString;
    }

    @Override
    public Collection<Class<? extends Throwable>> getFatalExceptions() {
        return fatalExceptions;
    }

    void makeRoot() {
        this.root = true;
    }

    @Override
    public Integer getSplit() {
        return split;
    }

    void setSplit(Integer split) {
        if(this.split != null) {
            if(!this.split.equals(split)) {
                throw new ConfigMismatchException("incompatible splits: " + this.split + " and " + split);
            }
        }
        this.split = split;
    }
}
