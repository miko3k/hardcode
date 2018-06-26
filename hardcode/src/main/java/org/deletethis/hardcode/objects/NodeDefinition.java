package org.deletethis.hardcode.objects;

import java.util.List;

public interface NodeDefinition {
    Class<?> getType();
    boolean isRoot();
    ConstructionStrategy getConstructionStrategy();
    List<NodeParameter> getParameters();
    String toString();
}
