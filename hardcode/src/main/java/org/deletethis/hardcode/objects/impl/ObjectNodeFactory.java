package org.deletethis.hardcode.objects.impl;

import org.deletethis.hardcode.objects.*;
import org.deletethis.hardcode.objects.impl.introspection.ConstructorWrapper;
import com.squareup.javapoet.CodeBlock;
import org.deletethis.hardcode.objects.impl.introspection.ParameterWrapper;
import org.deletethis.hardcode.util.TypeUtil;
import org.deletethis.hardcode.objects.impl.introspection.FieldInstrospectionStartegy;
import org.deletethis.hardcode.objects.impl.introspection.IntrospectionStartegy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;

public class ObjectNodeFactory implements NodeFactory {

    private IntrospectionStartegy strategy = new FieldInstrospectionStartegy();

    public class CallConstructor implements ConstructionStrategy {

        private final Class<?> clz;

        public CallConstructor(Class<?> clz) {
            this.clz = clz;
        }

        @Override
        public Expression getCode(CodegenContext context, List<Expression> arguments) {
            CodeBlock.Builder bld = CodeBlock.builder();
            bld.add("new $T(", clz);
            boolean first = true;
            for (Expression b : arguments) {
                if (first) {
                    first = false;
                } else {
                    bld.add(", ");
                }
                bld.add(b.getCode());
            }
            bld.add(")");
            return Expression.complex(bld.build());
        }

        @Override
        public String toString() {
            return clz.getSimpleName();
        }
    }
    
    @Override
    public boolean enableReferenceDetection() {
        return true;
    }


    
    private ConstructorWrapper findMatchingConstructor(Class<?> clz, Set<String> parameterNames) {
        ConstructorWrapper result = null;

        for(Constructor<?> c: clz.getDeclaredConstructors()) {
            if(!Modifier.isPublic(c.getModifiers()))
                continue;

            ConstructorWrapper constructorWrapper = new ConstructorWrapper(c);

            List<ParameterWrapper> parameters = constructorWrapper.getParameters();

            if(parameters.size() != parameterNames.size())
                continue;

            boolean ok = true;

            for(ParameterWrapper p: parameters) {
                if(!parameterNames.contains(p.getName())) {
                    ok = false;
                    break;
                }
            }
            if(ok) {
                if(result == null) {
                    result = constructorWrapper;
                } else {
                    throw new IllegalArgumentException(clz.getName() + ": more than one constructor matches parameter names: " + parameterNames);
                }
            }
        }
        if(result == null) {
            throw new IllegalArgumentException(clz.getName() + ": no constructor matches paramter names: " + parameterNames);
        } else {
            return result;
        }
    }


    @Override
    public Optional<NodeDef> createNode(Object someObject) {
        Objects.requireNonNull(someObject);

        Class<?> clz = someObject.getClass();
        if(clz.isSynthetic())
            return Optional.empty();
        
        Map<String, IntrospectionStartegy.Member> introspect = strategy.introspect(clz);
        ConstructorWrapper cons = findMatchingConstructor(clz, introspect.keySet());
        List<Object> arguments = new ArrayList<>();

        for(ParameterWrapper p: cons.getParameters()) {
            String name = p.getName();
            Class<?> type = p.getType();

            Object value = introspect.get(name).getValue(someObject);
            // IS THIS REALLY NECESSARY??
            if(value == null) {
                if (type.isPrimitive()) {
                    throw new IllegalArgumentException(clz.getName() + ": " + name + ": cannot assign null to " + type.getName());
                }
            } else {
                Class<?> valueClass = value.getClass();
                if(!TypeUtil.wrapType(type).isAssignableFrom(valueClass)) {
                    throw new IllegalArgumentException(clz.getName() + ": " + name + ": cannot assign " + valueClass.getName() + " to " + type.getName());
                }
            }
            arguments.add(value);
        }
        return Optional.of(new NodeDefImpl(clz, arguments, new CallConstructor(clz)));
    }

    @Override
    public int getOrdering() {
        return 100;
    }
}
