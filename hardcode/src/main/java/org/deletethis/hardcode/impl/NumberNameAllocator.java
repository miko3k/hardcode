package org.deletethis.hardcode.impl;

import com.squareup.javapoet.NameAllocator;

import java.beans.Introspector;
import java.util.HashMap;
import java.util.Map;

class NumberNameAllocator {
    private final NameAllocator nameAllocator = new NameAllocator();
    private final Map<String, Integer> map = new HashMap<>();

    private String addNumber(String base) {
        // we allocate plenty of variables with the same name,
        // let's add a number before NameAllocator starts adding underscores
        Integer n = map.get(base);
        String result;
        if (n == null) {
            result = base;
            n = 1;
        } else {
            result = base + n;
            ++n;
        }
        map.put(base, n);
        return result;
    }

    private String getSuggestion(Class<?> hint) {
        return Introspector.decapitalize(hint.getSimpleName());
    }

    String newName(Class<?> suggestion) {
        return newName(getSuggestion(suggestion));
    }

    String newName(String suggestion) {
        return nameAllocator.newName(addNumber(suggestion));
    }

    String newName(Class<?> suggestion, Object tag) {
        return newName(getSuggestion(suggestion), tag);
    }

    String newName(String suggestion, Object tag) {
        return nameAllocator.newName(addNumber(suggestion), tag);
    }

    String get(Object tag) {
        return nameAllocator.get(tag);
    }
}
