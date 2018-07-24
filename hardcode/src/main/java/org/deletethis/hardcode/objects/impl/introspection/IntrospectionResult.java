package org.deletethis.hardcode.objects.impl.introspection;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IntrospectionResult {
    Set<String> getMembers();
    List<Annotation> getMemberAnnotations(String memberName);
    Map<String, Object> getMemberValues(Object object);
}
