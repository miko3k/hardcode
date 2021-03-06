package org.deletethis.hardcode.util;

import java.util.*;

public class TypeUtil {
    private static final Map<Class<?>, Class<?>> PRIMITIVES_TO_WRAPPERS;

    static {
        PRIMITIVES_TO_WRAPPERS = new HashMap<>();
        PRIMITIVES_TO_WRAPPERS.put(boolean.class, Boolean.class);
        PRIMITIVES_TO_WRAPPERS.put(byte.class, Byte.class);
        PRIMITIVES_TO_WRAPPERS.put(char.class, Character.class);
        PRIMITIVES_TO_WRAPPERS.put(double.class, Double.class);
        PRIMITIVES_TO_WRAPPERS.put(float.class, Float.class);
        PRIMITIVES_TO_WRAPPERS.put(int.class, Integer.class);
        PRIMITIVES_TO_WRAPPERS.put(long.class, Long.class);
        PRIMITIVES_TO_WRAPPERS.put(short.class, Short.class);
        PRIMITIVES_TO_WRAPPERS.put(void.class, Void.class);
    }

    // https://stackoverflow.com/a/1704658
    // safe because both Long.class and long.class are of type Class<Long>
    @SuppressWarnings("unchecked")
    public static <T> Class<T> wrapType(Class<T> c) {
        return c.isPrimitive() ? (Class<T>) PRIMITIVES_TO_WRAPPERS.get(c) : c;
    }

    private static class AncestorIterator implements Iterator<Class<?>> {
        Set<Class<?>> visitedInterfaces = new HashSet<>();
        Deque<Class<?>> remaining = new ArrayDeque<>();

        private AncestorIterator(Class<?> clz) {
            remaining.addLast(clz);
        }

        @Override
        public boolean hasNext() {
            return !remaining.isEmpty();
        }

        @Override
        public Class<?> next() {
            Class<?> result = remaining.removeFirst();

            Class<?> superclass = result.getSuperclass();
            if(superclass != null) {
                // there's always a single parent class - no need to to track
                // visited or not
                remaining.addLast(superclass);
            }
            Class<?>[] interfaces = result.getInterfaces();
            for(Class<?> iface: interfaces) {
                if(!visitedInterfaces.contains(iface)) {
                    visitedInterfaces.add(iface);
                    remaining.addLast(iface);
                }

            }

            return result;
        }
    }

    /** Returns an {@link Iterable}, which will iterate over all ancestors of the given class, including superclasses
     * and superinterfaces.
     *
     * @param clz the target class
     * @return an {@link Iterable} going upwards in class hierarchy, returning each class or interface only once
     */
    public static Iterable<Class<?>> ancestors(Class<?> clz) {
        return () -> new AncestorIterator(clz);
    }

    /** Similar to {@link java.lang.Class#getSimpleName} but works also with anonymous classes */
    public static String getClassSimpleName(Class<?> clz) {
        String str = clz.getSimpleName();
        if(str.isEmpty()) {
            str = clz.getTypeName();
            int idx = str.lastIndexOf('.');
            if(idx >= 0)
                str = str.substring(idx+1);
        }
        return str;

    }
}
