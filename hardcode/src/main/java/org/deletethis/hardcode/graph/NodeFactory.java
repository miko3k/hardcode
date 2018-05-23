package org.deletethis.hardcode.graph;

import java.util.Optional;

public interface NodeFactory {
    boolean enableReferenceDetection();
    Optional<Node> createNode(NodeFactoryContext context, Object object);
    int getOrdering();
}
