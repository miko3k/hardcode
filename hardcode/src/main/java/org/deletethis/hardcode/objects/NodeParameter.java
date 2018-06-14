package org.deletethis.hardcode.objects;

import java.lang.annotation.Annotation;
import java.util.List;

public class NodeParameter {
    private final ParameterName parameterName;
    private final Object value;
    private final List<Annotation> annotations;

    public NodeParameter(ParameterName parameterName, Object value, List<Annotation> annotations) {
        this.parameterName = parameterName;
        this.value = value;
        this.annotations = annotations;
    }

    public NodeParameter(ParameterName parameterName, Object value) {
        this(parameterName, value, null);
    }

    public ParameterName getParameterName() {
        return parameterName;
    }

    public Object getValue() {
        return value;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }
}
