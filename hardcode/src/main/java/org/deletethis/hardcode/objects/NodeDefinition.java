package org.deletethis.hardcode.objects;

import java.util.Collection;

public interface NodeDefinition {
    Class<?> getType();
    ConstructionStrategy getConstructionStrategy();
    Collection<NodeParameter> getParameters();
    Collection<Class<? extends Throwable>> getFatalExceptions();
    String toString();
}
