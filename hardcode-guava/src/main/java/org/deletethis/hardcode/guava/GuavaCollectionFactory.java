package org.deletethis.hardcode.guava;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.CodeBlock;
import org.deletethis.hardcode.HardcodeConfiguration;
import org.deletethis.hardcode.objects.*;
import org.deletethis.hardcode.objects.impl.NodeDefImpl;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

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

    private Expression getCode(Class<?> clz, CodegenContext context, List<Expression> arguments) {
        // there are also longer variants, but's use builder for larger ones
        if (arguments.size() < 10) {
            CodeBlock.Builder cb = CodeBlock.builder();
            cb.add("$T.of(", clz);
            boolean first = true;
            for (Expression e : arguments) {
                if (first) {
                    first = false;
                } else {
                    cb.add(",");
                }
                cb.add(e.getCode());
            }
            cb.add(")");
            return Expression.complex(cb.build());
        } else {
            String variable = context.allocateVariable(clz);

            context.addStatement("$T.Builder $L = $T.builderWithExpectedSize($L)", clz, variable, clz, arguments.size());

            for (Expression arg : arguments) {
                context.addStatement("$L.add($L)", variable, arg.getCode());
            }
            return Expression.complex("$L.build()", variable);
        }
    }


    @Override
    public Optional<NodeDefinition> createNode(Object object, HardcodeConfiguration configuration, List<Annotation> annotations) {
        Class<?> aClass = findClass(object);

        if (aClass == null)
            return Optional.empty();

        Collection<?> coll = (Collection<?>) object;
        List<NodeParameter> members = coll.stream().map(NodeParameter::new).collect(Collectors.toList());
        return Optional.of(new NodeDefImpl(aClass, aClass.getSimpleName(), this::getCode, members));
    }

    @Override
    public int getOrdering() {
        return 0;
    }
}
