package org.deletethis.hardcode.guava;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.CodeBlock;
import org.deletethis.hardcode.HardcodeConfiguration;
import org.deletethis.hardcode.objects.*;
import org.deletethis.hardcode.objects.impl.NodeDefImpl;

import java.util.*;

public class GuavaCollectionFactory implements NodeFactory {
    private static final Set<Class<?>> CLASSES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            ImmutableList.class, ImmutableSet.class

    )));

    private Class<?> findClass(Object o) {
        for (Class<?> c : CLASSES) {
            if (c.isInstance(o))
                return c;
        }
        return null;
    }

    @Override
    public boolean enableReferenceDetection() {
        return false;
    }

    private Expression getCode(Class<?> clz, CodegenContext context, ObjectContext obj) {
        // there are also longer variants, but's use builder for larger ones
        if (obj.getArguments().size() < 10) {
            CodeBlock cb = GuavaUtil.printOf(clz, obj);
            return Expression.complex(cb);
        } else {
            String variable = context.allocateVariable(clz);

            context.addStatement("$T.Builder $L = $T.builderWithExpectedSize($L)", clz, variable, clz, obj.getArguments().size());

            for (Expression arg : obj) {
                context.addStatement("$L.add($L)", variable, arg.getCode());
            }
            return Expression.complex("$L.build()", variable);
        }
    }


    @Override
    public Optional<NodeDefinition> createNode(Object object, HardcodeConfiguration configuration) {
        Class<?> aClass = findClass(object);

        if (aClass == null)
            return Optional.empty();

        Collection<?> coll = (Collection<?>) object;
        int idx = 0;
        List<NodeParameter> members = new ArrayList<>(coll.size());
        for(Object o: coll) {
            members.add(new NodeParameter(new IndexParamteter(idx), o));
            ++idx;
        }
        return Optional.of(new NodeDefImpl(aClass, aClass.getSimpleName(), (context, obj) -> getCode(aClass, context, obj), members));
    }

    @Override
    public int getOrdering() {
        return 0;
    }
}
