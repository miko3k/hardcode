package org.deletethis.hardcode.objects.impl;

import com.squareup.javapoet.CodeBlock;

import org.deletethis.hardcode.HardcodeConfiguration;
import org.deletethis.hardcode.objects.*;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class PrimitiveNodeFactory implements NodeFactory {
    @Override
    public boolean enableReferenceDetection() {
        return false;
    }

    private <T> Optional<NodeDefinition> create(Class<?> clz, T value, Function<T, CodeBlock> fn) {
        return Optional.of(new NodeDefImpl(
                clz,
                String.valueOf(value),
                (clz1, context, arguments) -> Expression.simple(fn.apply(value))));
    }
    
    @Override
    public Optional<NodeDefinition> createNode(Object object, HardcodeConfiguration configuration, List<Annotation> annotations) {
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
