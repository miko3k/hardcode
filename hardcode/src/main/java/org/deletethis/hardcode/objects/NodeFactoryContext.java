package org.deletethis.hardcode.objects;

import java.util.Collections;
import java.util.List;

public interface NodeFactoryContext {
    NodeDefinition getNode(Object object);

    NodeDefinition createNode(Class<?> type, List<NodeDefinition> parameters, ConstructionStrategy constructor);

    default NodeDefinition createNode(Class<?> type, ConstructionStrategy constructor) {
        return createNode(type, Collections.emptyList(), constructor);
    }
}
