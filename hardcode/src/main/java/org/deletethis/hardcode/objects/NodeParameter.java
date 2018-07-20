package org.deletethis.hardcode.objects;

import java.lang.annotation.Annotation;
import java.util.List;

public interface NodeParameter {
    ParameterName getName();

    Object getValue();

    List<Annotation> getAnnotations();
}
