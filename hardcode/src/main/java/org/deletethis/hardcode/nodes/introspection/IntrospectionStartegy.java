package org.deletethis.hardcode.nodes.introspection;

import java.util.Map;

public interface IntrospectionStartegy {
    interface Member {
        Object getValue(Object object);
    }

    Map<String, Member> introspect(Class<?> clz);
}
