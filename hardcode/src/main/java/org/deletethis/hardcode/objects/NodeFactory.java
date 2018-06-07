package org.deletethis.hardcode.objects;

import org.deletethis.hardcode.HardcodeConfiguration;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

public interface NodeFactory {
    boolean enableReferenceDetection();
    Optional<NodeDefinition> createNode(Object object, HardcodeConfiguration configuration, List<Annotation> annotations);
    int getOrdering();
}
