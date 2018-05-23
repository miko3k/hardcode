package org.deletethis.hardcode.nodes;

import org.deletethis.hardcode.nodes.introspection.ConstructorWrapper;
import com.squareup.javapoet.CodeBlock;
import org.deletethis.hardcode.ParameterWrapper;
import org.deletethis.hardcode.Util;
import org.deletethis.hardcode.graph.Node;
import org.deletethis.hardcode.graph.NodeFactory;
import org.deletethis.hardcode.graph.NodeFactoryContext;
import org.deletethis.hardcode.nodes.introspection.FieldInstrospectionStartegy;
import org.deletethis.hardcode.nodes.introspection.IntrospectionStartegy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;
import org.deletethis.hardcode.CodegenContext;
import org.deletethis.hardcode.ConstructionStrategy;
import org.deletethis.hardcode.Expression;

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
            return clz.getName();
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
    public Optional<Node> createNode(NodeFactoryContext context, Object someObject) {
        Objects.requireNonNull(someObject);

        Class<?> clz = someObject.getClass();
        if(clz.isSynthetic())
            return Optional.empty();
        
        Map<String, IntrospectionStartegy.Member> introspect = strategy.introspect(clz);
        ConstructorWrapper cons = findMatchingConstructor(clz, introspect.keySet());
        List<Node> arguments = new ArrayList<>();

        for(ParameterWrapper p: cons.getParameters()) {
            String name = p.getName();
            Class<?> type = p.getType();

            Object value = introspect.get(name).getValue(someObject);

            if(value == null) {
                if (type.isPrimitive()) {
                    throw new IllegalArgumentException(clz.getName() + ": " + name + ": cannot assign null to " + type.getName());
                }
            } else {
                Class<?> valueClass = value.getClass();
                if(!Util.wrapType(type).isAssignableFrom(valueClass)) {
                    throw new IllegalArgumentException(clz.getName() + ": " + name + ": cannot assign " + valueClass.getName() + " to " + type.getName());
                }
            }
            Node n = context.getNode(value);
            arguments.add(n);
        }
        Node result = new Node(clz, arguments, new CallConstructor(clz));
        return Optional.of(result);
    }

    @Override
    public int getOrdering() {
        return 100;
    }
}
