package org.deletethis.hardcode.impl;

import org.deletethis.hardcode.HardcodeRoot;

import java.util.HashSet;
import java.util.Set;

class GraphBuilderUtil {
    private static boolean isRoot(Set<Class<?>> rootClasses, Set<Class<?>> alreadyChecked, Class<?> clz) {
        if(alreadyChecked.contains(clz)) {
            throw new IllegalArgumentException();
        }
        alreadyChecked.add(clz);
        if(clz.isAnnotationPresent(HardcodeRoot.class)) {
            return true;
        }
        if(rootClasses != null && rootClasses.contains(clz)) {
            return true;
        }
        Class<?> sup = clz.getSuperclass();
        if(sup != null && !alreadyChecked.contains(sup)) {
            if(isRoot(rootClasses, alreadyChecked, sup)) {
                return true;
            }
        }
        Class<?>[] ifaces = clz.getInterfaces();
        for(Class<?> iface: ifaces) {
            if(!alreadyChecked.contains(iface)) {
                if (isRoot(rootClasses, alreadyChecked, iface)) {
                    return true;
                }
            }
        }
        return false;
    }

    static boolean isRoot(Set<Class<?>> rootClasses, Class<?> clz) {
        return isRoot(rootClasses, new HashSet<>(), clz);
    }
}
