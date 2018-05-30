package org.deletethis.hardcode.objects.nodes.introspection;

import java.util.Map;

public interface IntrospectionStartegy {
    interface Member {
        Object getValue(Object object);
    }

    Map<String, Member> introspect(Class<?> clz);
}
