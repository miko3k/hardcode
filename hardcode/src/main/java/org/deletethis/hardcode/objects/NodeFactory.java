package org.deletethis.hardcode.objects;

import java.util.Optional;

public interface NodeFactory {
    boolean enableReferenceDetection();
    Optional<NodeDef> createNode(Object object);
    int getOrdering();
}
