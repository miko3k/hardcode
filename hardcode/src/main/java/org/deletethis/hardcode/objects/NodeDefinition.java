package org.deletethis.hardcode.objects;

import org.deletethis.hardcode.graph.ObjectInfo;

import java.util.List;

public interface NodeDefinition {
    ObjectInfo getObjectInfo();
    List<Object> getParameters();
}
