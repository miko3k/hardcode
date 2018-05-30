package org.deletethis.hardcode.objects.nodes;

import com.squareup.javapoet.CodeBlock;
import java.util.List;

import org.deletethis.hardcode.objects.NodeDefinition;
import org.deletethis.hardcode.objects.NodeFactory;
import org.deletethis.hardcode.objects.NodeFactoryContext;

import java.util.Optional;
import java.util.function.Function;
import org.deletethis.hardcode.objects.CodegenContext;
import org.deletethis.hardcode.objects.ConstructionStrategy;
import org.deletethis.hardcode.objects.Expression;

public class PrimitiveNodeFactory implements NodeFactory {
    private static class Literal<T> implements ConstructionStrategy {
        private final T value;
        private final Function<T, CodeBlock> fn;

        public Literal(T value, Function<T, CodeBlock> fn) {
            this.value = value;
            this.fn = fn;
        }

        @Override
        public Expression getCode(CodegenContext context, List<Expression> arguments) {
            return Expression.simple(fn.apply(value));
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    @Override
    public boolean enableReferenceDetection() {
        return false;
    }

    private <T> Optional<NodeDefinition> create(NodeFactoryContext context, Class<?> clz, T value, Function<T, CodeBlock> fn) {
        return Optional.of(context.createNode(clz, new Literal<>(value, fn)));
    }
    
    @Override
    public Optional<NodeDefinition> createNode(NodeFactoryContext context, Object object) {
        if(object instanceof Integer) {
            return create(context, Integer.class, (Integer)object, (val)->CodeBlock.of("$L", val));
        }
        if(object instanceof Long) {
            return create(context, Long.class, (Long)object, (val)->CodeBlock.of("$LL", val));
        }
        if(object instanceof Boolean) {
            return create(context, Boolean.class, (Boolean)object, (val)->CodeBlock.of("$L", val));
        }
        // string is also primitive
        if(object instanceof String) {
            return create(context, String.class, (String)object, (val)->CodeBlock.of("$S", val));
        }
        if(object instanceof Enum) {
            Class<?> clz = object.getClass();
            return create(context, clz, (Enum<?>)object, (val)->CodeBlock.of("$T.$L", clz, val));
        }
        
        return Optional.empty();
    }

    @Override
    public int getOrdering() {
        return 0;
    }
}
