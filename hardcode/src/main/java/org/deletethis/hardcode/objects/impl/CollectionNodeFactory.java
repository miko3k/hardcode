package org.deletethis.hardcode.objects.impl;

import org.deletethis.hardcode.objects.*;

import java.util.*;

public class CollectionNodeFactory implements NodeFactory {
    private static final Set<Class> CLASSES_WITH_CAPACITY = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            ArrayList.class, HashSet.class, ArrayDeque.class, LinkedHashSet.class, Vector.class

    )));

    private static final Set<Class> CLASSES_WITHOUT_CAPACITY = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            LinkedList.class, TreeSet.class
    )));


    @Override
    public boolean enableReferenceDetection() {
        return true;
    }

    private Expression getCode(Class<?> clz, CodegenContext context, List<Expression> arguments) {
        String variable = context.allocateVariable(clz.getSimpleName());
        if (CLASSES_WITH_CAPACITY.contains(clz)) {
            context.getBody().addStatement("$T $L = new $T($L)", clz, variable, clz, arguments.size());
        } else {
            context.getBody().addStatement("$T $L = new $T()", clz, variable, clz);
        }
        for (Expression arg : arguments) {
            context.getBody().addStatement("$L.add($L)", variable, arg.getCode());
        }
        return Expression.simple(variable);
    }

    @Override
    public Optional<NodeDefinition> createNode(Object object) {
        Class<?> aClass = object.getClass();

        if(!CLASSES_WITH_CAPACITY.contains(aClass) && !CLASSES_WITHOUT_CAPACITY.contains(aClass))
            return Optional.empty();

        Collection<?> coll = (Collection<?>)object;
        List<Object> members = new ArrayList<>(coll);
        return Optional.of(new NodeDefImpl(aClass, aClass.getSimpleName(), members, this::getCode));
    }

    @Override
    public int getOrdering() {
        return 0;
    }
}
