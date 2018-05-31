package org.deletethis.hardcode.guava;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.CodeBlock;
import org.deletethis.hardcode.objects.*;
import org.deletethis.hardcode.objects.impl.ConstructionStrategy;
import org.deletethis.hardcode.objects.impl.NodeDefImpl;

import java.util.*;

public class GuavaCollectionFactory implements NodeFactory {
    private static final Set<Class<?>> CLASSES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            ImmutableList.class, ImmutableSet.class

    )));

    private Class<?> findClass(Object o) {
        for(Class<?> c: CLASSES) {
            if(c.isInstance(o))
                return c;
        }
        return null;
    }

    @Override
    public boolean enableReferenceDetection() {
        return true;
    }

    private static class EmitCode implements ConstructionStrategy {
        private final Class<?> clz;

        EmitCode(Class<?> clz) {
            this.clz = clz;
        }

        @Override
        public Expression getCode(CodegenContext context, List<Expression> arguments) {
            // there are also longer variants, but's use builder for larger ones
            if(arguments.size() < 10) {
                CodeBlock.Builder cb = CodeBlock.builder();
                cb.add("$T.of(", clz);
                boolean first = true;
                for(Expression e: arguments) {
                    if(first) {
                        first = false;
                    } else {
                        cb.add(",");
                    }
                    cb.add(e.getCode());
                }
                cb.add(")");
                return Expression.complex(cb.build());
            } else {
                String variable = context.allocateVariable(clz.getSimpleName());

                context.getBody().addStatement("$T.Builder $L = $T.builderWithExpectedSize($L)", clz, variable, clz, arguments.size());

                for(Expression arg: arguments) {
                    context.getBody().addStatement("$L.add($L)", variable, arg.getCode());
                }
                return Expression.complex("$L.build()", variable);
            }
        }

        @Override
        public String toString() {
            return clz.getSimpleName();
        }
    }


    @Override
    public Optional<NodeDef> createNode(Object object) {
        Class<?> aClass = findClass(object);

        if(aClass == null)
            return Optional.empty();

        Collection<?> coll = (Collection<?>)object;
        List<Object> members = new ArrayList<>(coll);
        return Optional.of(new NodeDefImpl(aClass, members, new EmitCode(aClass)));
    }

    @Override
    public int getOrdering() {
        return 0;
    }
}
