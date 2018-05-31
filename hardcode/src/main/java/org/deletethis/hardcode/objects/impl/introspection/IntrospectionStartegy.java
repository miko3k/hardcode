package org.deletethis.hardcode.objects.impl.introspection;

import java.util.Map;

public interface IntrospectionStartegy {
    interface Member {
        Object getValue(Object object);
    }

    Map<String, Member> introspect(Class<?> clz);
}
