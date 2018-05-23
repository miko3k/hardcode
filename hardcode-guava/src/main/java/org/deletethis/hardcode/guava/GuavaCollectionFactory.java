package org.deletethis.hardcode.guava;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.CodeBlock;
import org.deletethis.hardcode.CodegenContext;
import org.deletethis.hardcode.ConstructionStrategy;
import org.deletethis.hardcode.Expression;
import org.deletethis.hardcode.graph.Node;
import org.deletethis.hardcode.graph.NodeFactory;
import org.deletethis.hardcode.graph.NodeFactoryContext;

import java.util.*;

public class GuavaCollectionFactory implements NodeFactory {
    final private static Set<Class<?>> CLASSES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
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
            return clz.getName();
        }
    }


    @Override
    public Optional<Node> createNode(NodeFactoryContext context, Object object) {
        Class<?> aClass = findClass(object);

        if(aClass == null)
            return Optional.empty();

        Collection<?> coll = (Collection<?>)object;
        List<Node> members = new ArrayList<>(coll.size());
        for(Object o: coll) {
            members.add(context.getNode(o));
        }
        return Optional.of(new Node(aClass, members, new EmitCode(aClass)));
    }

    @Override
    public int getOrdering() {
        return 0;
    }
}
