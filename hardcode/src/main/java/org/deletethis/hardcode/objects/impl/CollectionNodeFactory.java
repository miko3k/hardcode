package org.deletethis.hardcode.objects.impl;

import org.deletethis.hardcode.HardcodeConfiguration;
import org.deletethis.hardcode.objects.*;
import org.deletethis.hardcode.util.SplitHelper;

import java.util.*;

public class CollectionNodeFactory implements NodeFactory {
    private static final Set<Class> CLASSES_WITH_CAPACITY = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            ArrayList.class, HashSet.class, ArrayDeque.class, LinkedHashSet.class, Vector.class

    )));

    private static final Set<Class> CLASSES_WITHOUT_CAPACITY = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            LinkedList.class, TreeSet.class
    )));


    private Expression getCode(Class<?> clz, CodegenContext context, CodegenParameters objectContext) {
        String variable = context.allocateVariable(clz);
        if (CLASSES_WITH_CAPACITY.contains(clz)) {
            context.addStatement("$T $L = new $T($L)", clz, variable, clz, objectContext.getArgumentList().size());
        } else {
            context.addStatement("$T $L = new $T()", clz, variable, clz);
        }
        SplitHelper splitHelper = SplitHelper.get(context, clz.getSimpleName(), objectContext.getSplit(), variable, clz);
        for (CodegenParameters.Argument arg : objectContext.getArgumentList()) {
            splitHelper.addStatement("$L.add($L)", splitHelper.getBuilder(), arg.getCode());
        }
        splitHelper.finish();
        return Expression.simple(variable);
    }

    @Override
    public Optional<NodeDefinition> createNode(Object object, HardcodeConfiguration configuration) {
        Class<?> aClass = object.getClass();

        if(!CLASSES_WITH_CAPACITY.contains(aClass) && !CLASSES_WITHOUT_CAPACITY.contains(aClass))
            return Optional.empty();

        NodeDefImpl def = NodeDefImpl.ref(aClass, aClass.getSimpleName(), (context, obj) -> getCode(aClass, context, obj));

        Collection<?> coll = (Collection<?>)object;
        int idx = 0;
        for(Object obj: coll) {
            def.addParameter(new IndexParamteter(idx), obj);
            ++idx;
        }

        return Optional.of(def);
    }

    @Override
    public int getOrdering() {
        return 0;
    }
}
