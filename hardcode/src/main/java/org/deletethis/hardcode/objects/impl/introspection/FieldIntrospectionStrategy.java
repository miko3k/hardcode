package org.deletethis.hardcode.objects.impl.introspection;

import org.deletethis.hardcode.HardcodeException;
import org.deletethis.hardcode.HardcodeIgnore;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class FieldIntrospectionStrategy implements IntrospectionStartegy {
    private static class ResultImpl implements IntrospectionResult {
        private final Map<String, List<Annotation>> memberAnnotations;
        private final Map<String, Field> memberFields;

        public ResultImpl(Map<String, Field> memberFields) {
            this.memberFields = memberFields;

            HashMap<String, List<Annotation>> ann = new HashMap<>();

            for(Map.Entry<String, Field> e: memberFields.entrySet()) {
                ann.put(e.getKey(), Arrays.asList(e.getValue().getAnnotations()));
            }
            this.memberAnnotations = Collections.unmodifiableMap(ann);
        }

        @Override
        public List<Annotation> getMemberAnnotations(String memberName) {
            List<Annotation> annotations = memberAnnotations.get(memberName);
            return Objects.requireNonNull(annotations, "invalid member name: " + memberName);
        }

        @Override
        public Map<String, Object> getMemberValues(Object object) {
            Map<String, Object> result = new HashMap<>(memberFields.size()*2);
            try {
                for(Map.Entry<String, Field> e: memberFields.entrySet()) {
                    String name = e.getKey();
                    Field field = e.getValue();

                    Object value = field.get(Objects.requireNonNull(object));
                    result.put(name, value);
                }
            } catch (IllegalAccessException e) {
                throw new HardcodeException(e);
            }
            return result;
        }

        @Override
        public Set<String> getMembers() {
            return memberAnnotations.keySet();
        }
    }

    @Override
    public IntrospectionResult introspect(Class<?> clz) {
        Map<String, Field> memberFields = new HashMap<>();

        while(clz != null) {
            Field[] fields = clz.getDeclaredFields();
            for(Field field : fields) {
                field.setAccessible(true);

                if(field.isAnnotationPresent(HardcodeIgnore.class))
                    continue;

                int modifiers = field.getModifiers();

                if(field.isSynthetic())
                    continue;

                if(Modifier.isStatic(modifiers))
                    continue;

                if(Modifier.isTransient(modifiers))
                    continue;

                if(memberFields.containsKey(field.getName())) {
                    throw new HardcodeException(clz.getName() + ": duplicate field: " + field.getName() + " in " + clz.getSimpleName());
                }
                memberFields.put(field.getName(), field);
            }
            clz = clz.getSuperclass();
        }
        return new ResultImpl(memberFields);
    }
}
