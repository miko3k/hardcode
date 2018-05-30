package org.deletethis.hardcode.objects;

import java.util.Optional;

public interface NodeFactory {
    boolean enableReferenceDetection();
    Optional<NodeDefinition> createNode(NodeFactoryContext context, Object object);
    int getOrdering();
}
