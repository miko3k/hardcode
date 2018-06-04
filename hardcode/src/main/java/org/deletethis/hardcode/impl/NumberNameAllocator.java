package org.deletethis.hardcode.impl;

import com.squareup.javapoet.NameAllocator;

import java.util.HashMap;
import java.util.Map;

public class NumberNameAllocator {
    private final NameAllocator nameAllocator = new NameAllocator();
    private final Map<String, Integer> map = new HashMap<>();

    String newName(String base) {
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
}
