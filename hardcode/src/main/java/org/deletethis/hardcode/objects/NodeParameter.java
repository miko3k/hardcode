package org.deletethis.hardcode.objects;

import java.lang.annotation.Annotation;
import java.util.List;

public class NodeParameter {
    private final Object value;
    private final List<Annotation> annotations;

    public NodeParameter(Object value, List<Annotation> annotations) {
        this.value = value;
        this.annotations = annotations;
    }

    public NodeParameter(Object value) {
        this.value = value;
        this.annotations = null;
    }

    public Object getValue() {
        return value;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }
}
