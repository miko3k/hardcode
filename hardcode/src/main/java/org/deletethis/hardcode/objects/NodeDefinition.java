package org.deletethis.hardcode.objects;

import org.deletethis.hardcode.ObjectInfo;

import java.util.List;

public interface NodeDefinition {
    ObjectInfo getObjectInfo();
    List<NodeParameter> getParameters();
}
