package org.deletethis.hardcode.objects.impl;

import org.deletethis.hardcode.HardcodeConfiguration;
import org.deletethis.hardcode.objects.*;

import java.lang.annotation.Annotation;
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

    private Expression getCode(Class<?> clz, CodegenContext context, ObjectContext obj) {
        List<Expression> arguments = obj.getArguments();
        String variable = context.allocateVariable(clz);
        if (CLASSES_WITH_CAPACITY.contains(clz)) {
            // arguments contain both keys and values, we create map with double initial capacitity
            // 1.33 should be enough at default load factor of of 0.75 but what the heck
            context.addStatement("$T $L = new $T($L)", clz, variable, clz, arguments.size());
        } else {
            context.addStatement("$T $L = new $T()", clz, variable, clz);
        }
        for (int i = 0; i < arguments.size(); i += 2) {
            context.addStatement("$L.put($L, $L)", variable, arguments.get(i).getCode(), arguments.get(i + 1).getCode());
        }
        return Expression.simple(variable);
    }


    @Override
    public Optional<NodeDefinition> createNode(Object object, HardcodeConfiguration configuration, List<Annotation> annotations) {
        Class<?> aClass = object.getClass();

        if(!CLASSES_WITH_CAPACITY.contains(aClass) && !CLASSES_WITHOUT_CAPACITY.contains(aClass))
            return Optional.empty();

        Map<?,?> coll = (Map<?,?>)object;
        List<NodeParameter> members = new ArrayList<>(coll.size()*2);

        int idx = 0;
        for(Map.Entry<?,?> o: coll.entrySet()) {
            members.add(new NodeParameter(new MapParameter(true, idx), o.getKey()));
            members.add(new NodeParameter(new MapParameter(false, idx), o.getValue()));
            ++idx;
        }
        return Optional.of(new NodeDefImpl(aClass, aClass.getSimpleName(), (context, obj) -> getCode(aClass, context, obj), members));
    }

    @Override
    public int getOrdering() {
        return 0;
    }
}
