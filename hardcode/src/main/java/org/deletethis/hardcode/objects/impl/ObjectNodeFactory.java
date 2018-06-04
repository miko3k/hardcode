package org.deletethis.hardcode.objects.impl;

import org.deletethis.hardcode.HardcodeConfiguration;
import org.deletethis.hardcode.HardcodeRoot;
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

    private Expression getCode(Class<?> clz, CodegenContext context, List<Expression> arguments) {
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

    private boolean isRoot(Set<Class<?>> rootClasses, Set<Class<?>> alreadyChecked, Class<?> clz) {
        if(alreadyChecked.contains(clz)) {
            throw new IllegalArgumentException();
        }
        alreadyChecked.add(clz);
        if(clz.isAnnotationPresent(HardcodeRoot.class)) {
            return true;
        }
        if(rootClasses != null && rootClasses.contains(clz)) {
            return true;
        }
        Class<?> sup = clz.getSuperclass();
        if(sup != null && !alreadyChecked.contains(sup)) {
            if(isRoot(rootClasses, alreadyChecked, sup)) {
                return true;
            }
        }
        Class<?>[] ifaces = clz.getInterfaces();
        for(Class<?> iface: ifaces) {
            if(!alreadyChecked.contains(iface)) {
                if (isRoot(rootClasses, alreadyChecked, iface)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Optional<NodeDefinition> createNode(Object object, HardcodeConfiguration configuration) {
        Objects.requireNonNull(object);

        Class<?> clz = object.getClass();
        if(clz.isSynthetic())
            return Optional.empty();

        Set<Class<?>> rootClasses = null;
        if(configuration instanceof IntrospectionConfiguration) {
            rootClasses = ((IntrospectionConfiguration)configuration).getHardcodeRoots();
        }


        boolean root = isRoot(rootClasses, new HashSet<>(), clz);

        Map<String, IntrospectionStartegy.Member> introspect = strategy.introspect(clz);
        ConstructorWrapper cons = findMatchingConstructor(clz, introspect.keySet());
        List<Object> arguments = new ArrayList<>();

        for(ParameterWrapper p: cons.getParameters()) {
            String name = p.getName();
            Class<?> type = p.getType();

            Object value = introspect.get(name).getValue(object);
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
        return Optional.of(new NodeDefImpl(clz, TypeUtil.simpleToString(object), this::getCode, arguments, root));
    }

    @Override
    public int getOrdering() {
        return 100;
    }
}
