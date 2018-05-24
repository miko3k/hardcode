package org.deletethis.hardcode.nodes.introspection;

import org.deletethis.hardcode.HardcodeException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class FieldInstrospectionStartegy implements IntrospectionStartegy {
    private static class MemberImpl implements Member {
        private final Field field;

        private MemberImpl(Field field) {
            this.field = field;
        }

        @Override
        public Object getValue(Object object) {
            try {
                return field.get(Objects.requireNonNull(object));
            } catch (IllegalAccessException e) {
                throw new HardcodeException(e);
            }
        }
    }

    @Override
    public Map<String, IntrospectionStartegy.Member> introspect(Class<?> clz) {
        Map<String, IntrospectionStartegy.Member> result = new HashMap<>();

        while(clz != null) {
            Field[] fields = clz.getDeclaredFields();
            for(Field field : fields) {
                if(!field.isAccessible())
                    field.setAccessible(true);

                int modifiers = field.getModifiers();

                if(field.isSynthetic())
                    continue;

                if(Modifier.isStatic(modifiers))
                    continue;

                if(Modifier.isTransient(modifiers))
                    continue;

                if(result.containsKey(field.getName())) {
                    throw new HardcodeException(clz.getName() + ": duplicate field: " + field.getName());
                }
                result.put(field.getName(), new MemberImpl(field));
            }
            clz = clz.getSuperclass();
        }
        return result;
    }
}
