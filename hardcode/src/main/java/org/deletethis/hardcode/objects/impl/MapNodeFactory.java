package org.deletethis.hardcode.objects.impl;

import org.deletethis.hardcode.HardcodeConfiguration;
import org.deletethis.hardcode.objects.*;
import org.deletethis.hardcode.util.SplitHelper;

import java.util.*;

public class MapNodeFactory implements NodeFactory {
    private static final Set<Class> CLASSES_WITH_CAPACITY = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            HashMap.class, Hashtable.class, LinkedHashMap.class
    )));

    private static final Set<Class> CLASSES_WITHOUT_CAPACITY = Collections.singleton(
            TreeMap.class
    );


    private Expression getCode(Class<?> clz, CodegenContext context, CodegenParameters obj) {
        List<CodegenParameters.Argument> arguments = obj.getArgumentList();
        String variable = context.allocateVariable(clz);
        if (CLASSES_WITH_CAPACITY.contains(clz)) {
            // arguments contain both keys and values, we create map with double initial capacitity
            // 1.33 should be enough at default load factor of of 0.75 but what the heck
            context.addStatement("$T $L = new $T($L)", clz, variable, clz, arguments.size());
        } else {
            context.addStatement("$T $L = new $T()", clz, variable, clz);
        }
        SplitHelper splitHelper = SplitHelper.get(context, clz.getSimpleName(), obj.getSplit(), variable, clz);

        for (int i = 0; i < arguments.size(); i += 2) {
            splitHelper.addStatement("$L.put($L, $L)", splitHelper.getBuilder(), arguments.get(i).getCode(), arguments.get(i + 1).getCode());
        }
        splitHelper.finish();
        return Expression.simple(variable);
    }


    @Override
    public Optional<NodeDefinition> createNode(Object object, HardcodeConfiguration configuration) {
        Class<?> aClass = object.getClass();

        if(!CLASSES_WITH_CAPACITY.contains(aClass) && !CLASSES_WITHOUT_CAPACITY.contains(aClass))
            return Optional.empty();

        Map<?,?> coll = (Map<?,?>)object;

        NodeDefImpl nodeDef = NodeDefImpl.ref(aClass, aClass.getSimpleName(), (context, obj) -> getCode(aClass, context, obj));

        int idx = 0;
        for(Map.Entry<?,?> o: coll.entrySet()) {
            nodeDef.addParameter(new MapParameter(true, idx), o.getKey());
            nodeDef.addParameter(new MapParameter(false, idx), o.getValue());
            ++idx;
        }
        return Optional.of(nodeDef);
    }

    @Override
    public int getOrdering() {
        return 0;
    }
}
