package org.deletethis.hardcode.objects.impl;

import org.deletethis.hardcode.HardcodeConfiguration;
import org.deletethis.hardcode.objects.*;
import org.deletethis.hardcode.util.TypeUtil;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

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

    private Expression getCode(Class<?> clz, CodegenContext context, ObjectContext arguments) {
        String variable = context.allocateVariable(clz);
        if (CLASSES_WITH_CAPACITY.contains(clz)) {
            context.addStatement("$T $L = new $T($L)", clz, variable, clz, arguments.getArguments().size());
        } else {
            context.addStatement("$T $L = new $T()", clz, variable, clz);
        }
        for (Expression arg : arguments) {
            context.addStatement("$L.add($L)", variable, arg.getCode());
        }
        return Expression.simple(variable);
    }

    @Override
    public Optional<NodeDefinition> createNode(Object object, HardcodeConfiguration configuration, List<Annotation> annotations) {
        Class<?> aClass = object.getClass();

        if(!CLASSES_WITH_CAPACITY.contains(aClass) && !CLASSES_WITHOUT_CAPACITY.contains(aClass))
            return Optional.empty();

        Collection<?> coll = (Collection<?>)object;
        List<NodeParameter> members = new ArrayList<>(coll.size());
        int idx = 0;
        for(Object obj: coll) {
            members.add(new NodeParameter(new IndexParamteter(idx), obj));
            ++idx;
        }

        return Optional.of(new NodeDefImpl(aClass, TypeUtil.simpleToString(object), (context, obj) -> getCode(aClass, context, obj), members));
    }

    @Override
    public int getOrdering() {
        return 0;
    }
}
