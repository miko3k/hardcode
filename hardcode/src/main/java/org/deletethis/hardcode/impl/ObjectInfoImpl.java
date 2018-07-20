package org.deletethis.hardcode.impl;

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

    public ObjectInfoImpl(Class<?> type, CodeGenerator codeGenerator, boolean root, Integer split, String asString, Collection<Class<? extends Throwable>> fatalExceptions) {
        this.type = type;
        this.codeGenerator = Objects.requireNonNull(codeGenerator);
        this.root = root;
        this.split = split;
        this.asString = Objects.requireNonNull(asString);
        this.fatalExceptions = (fatalExceptions == null) ? Collections.emptyList() : fatalExceptions;
    }

    static ObjectInfoImpl ofNodeDefinion(NodeDefinition def) {
        return new ObjectInfoImpl(
                def.getType(),
                def.getConstructionStrategy(),
                false,
                null,
                def.toString(),
                def.getFatalExceptions()
        );
    }

    static ObjectInfoImpl ofNull() {
        return new ObjectInfoImpl(
                null,
                (a,b) -> Expression.simple("null"),
                false,
                null,
                "null",
                null);
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
        this.split = split;
    }
}
