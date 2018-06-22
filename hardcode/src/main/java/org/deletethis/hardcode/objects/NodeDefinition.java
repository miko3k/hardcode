package org.deletethis.hardcode.objects;

import java.util.List;

public interface NodeDefinition {
    ObjectInfo getObjectInfo();
    List<NodeParameter> getParameters();
}
