package org.deletethis.hardcode.objects;

import java.lang.annotation.Annotation;
import java.util.List;

public interface NodeParameter {
    ParameterName getParameterName();

    Object getValue();

    List<Annotation> getAnnotations();
}
