package org.deletethis.hardcode.impl;

import org.deletethis.hardcode.objects.CodeGenerator;
import org.deletethis.hardcode.objects.Expression;
import org.deletethis.hardcode.objects.NodeDefinition;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

class ObjectInfoNull implements ObjectInfo {
    @Override
    public Class<?> getType() {
        return null;
    }

    @Override
    public CodeGenerator getCodeGenerator() {
        return (a,b) -> Expression.simple("null");
    }

    @Override
    public boolean isRoot() {
        return false;
    }

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public Collection<Class<? extends Throwable>> getFatalExceptions() {
        return Collections.emptyList();
    }

    @Override
    public Integer getSplit() {
        return  null;
    }
}
