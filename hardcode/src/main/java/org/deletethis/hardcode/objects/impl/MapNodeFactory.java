package org.deletethis.hardcode.objects.impl;

import org.deletethis.hardcode.objects.*;

import java.util.*;

public class MapNodeFactory implements NodeFactory {
    final private static Set<Class> CLASSES_WITH_CAPACITY = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            HashMap.class, Hashtable.class, LinkedHashMap.class
    )));

    final private static Set<Class> CLASSES_WITHOUT_CAPACITY = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            TreeMap.class
    )));


    @Override
    public boolean enableReferenceDetection() {
        return true;
    }

    private Expression getCode(Class<?> clz, CodegenContext context, List<Expression> arguments) {
        String variable = context.allocateVariable(clz.getSimpleName());
        if (CLASSES_WITH_CAPACITY.contains(clz)) {
            // arguments contain both keys and values, we create map with double initial capacitity
            // 1.33 should be enough at default load factor of of 0.75 but what the heck
            context.getBody().addStatement("$T $L = new $T($L)", clz, variable, clz, arguments.size());
        } else {
            context.getBody().addStatement("$T $L = new $T()", clz, variable, clz);
        }
        for (int i = 0; i < arguments.size(); i += 2) {
            context.getBody().addStatement("$L.put($L, $L)", variable, arguments.get(i).getCode(), arguments.get(i + 1).getCode());
        }
        return Expression.simple(variable);
    }


    @Override
    public Optional<NodeDefinition> createNode(Object object) {
        Class<?> aClass = object.getClass();

        if(!CLASSES_WITH_CAPACITY.contains(aClass) && !CLASSES_WITHOUT_CAPACITY.contains(aClass))
            return Optional.empty();

        Map<?,?> coll = (Map<?,?>)object;
        List<Object> members = new ArrayList<>(coll.size());
        for(Map.Entry<?,?> o: coll.entrySet()) {
            members.add(o.getKey());
            members.add(o.getValue());
        }
        return Optional.of(new NodeDefImpl(aClass, aClass.getSimpleName(), members, this::getCode));
    }

    @Override
    public int getOrdering() {
        return 0;
    }
}
