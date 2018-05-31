package org.deletethis.hardcode.objects;

import org.deletethis.hardcode.graph.ObjectInfo;

import java.util.List;

public interface NodeDef {
    ObjectInfo getObjectInfo();
    List<Object> getParameters();
}
