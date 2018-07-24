package org.deletethis.hardcode.objects.impl.introspection;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

public interface IntrospectionStartegy {
    IntrospectionResult introspect(Class<?> clz);
}
