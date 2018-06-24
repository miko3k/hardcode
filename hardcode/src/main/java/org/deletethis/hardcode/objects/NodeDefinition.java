package org.deletethis.hardcode.objects;

import org.deletethis.hardcode.objects.impl.ConstructionStrategy;

import java.util.List;

public interface NodeDefinition {
    Class<?> getType();
    boolean isRoot();
    ConstructionStrategy getConstructionStrategy();
    List<NodeParameter> getParameters();
    String toString();
}
