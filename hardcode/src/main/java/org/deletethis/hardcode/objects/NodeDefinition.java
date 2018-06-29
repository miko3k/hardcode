package org.deletethis.hardcode.objects;

import java.util.Collection;

public interface NodeDefinition {
    Class<?> getType();
    boolean isRoot();
    ConstructionStrategy getConstructionStrategy();
    Collection<NodeParameter> getParameters();
    Collection<Class<?>> getFatalExceptions();
    String toString();
}
