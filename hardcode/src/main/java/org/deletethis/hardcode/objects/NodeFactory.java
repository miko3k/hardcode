package org.deletethis.hardcode.objects;

import org.deletethis.hardcode.HardcodeConfiguration;

import java.util.Optional;

public interface NodeFactory {
    boolean enableReferenceDetection();
    Optional<NodeDefinition> createNode(Object object, HardcodeConfiguration configuration);
    int getOrdering();
}
