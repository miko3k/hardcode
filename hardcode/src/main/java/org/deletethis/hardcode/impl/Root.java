package org.deletethis.hardcode.impl;

import org.deletethis.hardcode.ObjectInfo;
import org.deletethis.hardcode.graph.Divertex;

import java.util.List;

class Root {
    Divertex<ObjectInfo> vertex;
    List<Divertex<ObjectInfo>> dependencies;
    String methodName;

    public Root(Divertex<ObjectInfo> vertex, String methodName, List<Divertex<ObjectInfo>> dependencies) {
        this.methodName = methodName;
        this.vertex = vertex;
        this.dependencies = dependencies;
    }

    public String toString() {
        return vertex.getPayload().toString() + " (" + methodName + "): " + dependencies;
    }

    public Divertex<ObjectInfo> getVertex() {
        return vertex;
    }
}