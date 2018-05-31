package org.deletethis.hardcode.objects.impl;

import com.squareup.javapoet.CodeBlock;
import java.util.List;

import org.deletethis.hardcode.objects.*;

import java.util.Optional;
import java.util.function.Function;

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

    private <T> Optional<NodeDef> create(Class<?> clz, T value, Function<T, CodeBlock> fn) {
        return Optional.of(new NodeDefImpl(clz, new Literal<>(value, fn)));
    }
    
    @Override
    public Optional<NodeDef> createNode(Object object) {
        if(object instanceof Integer) {
            return create(Integer.class, object, (val)->CodeBlock.of("$L", val));
        }
        if(object instanceof Long) {
            return create(Long.class, object, (val)->CodeBlock.of("$LL", val));
        }
        if(object instanceof Boolean) {
            return create(Boolean.class, object, (val)->CodeBlock.of("$L", val));
        }
        // string is also primitive
        if(object instanceof String) {
            return create(String.class, object, (val)->CodeBlock.of("$S", val));
        }
        if(object instanceof Enum) {
            Class<?> clz = object.getClass();
            return create(clz, object, (val)->CodeBlock.of("$T.$L", clz, val));
        }
        
        return Optional.empty();
    }

    @Override
    public int getOrdering() {
        return 0;
    }
}
