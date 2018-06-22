package org.deletethis.hardcode.objects.impl.introspection;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

public interface IntrospectionStartegy {
    interface Member {
        Object getValue(Object object);
        List<Annotation> getAnnotations();
    }

    Map<String, Member> introspect(Class<?> clz);
}
