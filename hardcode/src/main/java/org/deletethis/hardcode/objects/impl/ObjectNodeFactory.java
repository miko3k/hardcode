package org.deletethis.hardcode.objects.impl;

import org.deletethis.hardcode.HardcodeConfiguration;
import org.deletethis.hardcode.HardcodeException;
import org.deletethis.hardcode.objects.*;
import org.deletethis.hardcode.objects.impl.introspection.*;
import com.squareup.javapoet.CodeBlock;
import org.deletethis.hardcode.util.TypeUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

public class ObjectNodeFactory implements NodeFactory {

    private IntrospectionStartegy strategy = new FieldIntrospectionStartegy();

    private Expression getCode(Class<?> clz, CodegenParameters obj) {
        CodeBlock.Builder bld = CodeBlock.builder();
        bld.add("new $T(", clz);
        int n = 0;
        for (CodegenParameters.Argument b : obj.getArgumentList()) {
            if(n > 0) {
                bld.add(", ");
            }
            bld.add("/*$L*/ ", b.getName());
            bld.add(b.getCode());
            ++n;
        }
        bld.add(")");
        return Expression.complex(bld.build());
    }

    private LinkedHashMap<String, Object> matchConstructor(Constructor<?> c, Map<String, Object> parameters) {
        if(Modifier.isPrivate(c.getModifiers()))
            return null;

        Parameter[] constructorParameters = c.getParameters();

        if(constructorParameters.length != parameters.size())
            return null;

        LinkedHashMap<String, Object> orderedParameters = new LinkedHashMap<>();
        Class<?> clz = c.getDeclaringClass();

        for(Parameter p: constructorParameters) {
            if(!p.isNamePresent())
                throw new HardcodeException(clz.getName() + ": constructor parameter names not present");

            String name = p.getName();

            if(!parameters.containsKey(name)) {
                return null;
            }

            Object value = parameters.get(name);
            Class<?> type = p.getType();

            if(value == null) {
                if (type.isPrimitive()) {
                    return null;
                }
            } else {
                Class<?> valueClass = value.getClass();
                if(!TypeUtil.wrapType(type).isAssignableFrom(valueClass)) {
                    return null;
                }
            }
            orderedParameters.put(name, value);

        }
        // should be true, map keys are unique, parameters names too and size matches
        assert orderedParameters.keySet().containsAll(parameters.keySet());

        return orderedParameters;
    }

    private String paramToString(Map.Entry<String, Object> param) {
        if(param.getValue() == null) {
            return param.getKey() + " = null";
        } else {
            // I think output is more readable like this, without invoking toString
            return param.getKey() + " = " + TypeUtil.getClassSimpleName(param.getValue().getClass());
        }
    }

    private String describeParameters(Map<String, Object> parameters) {
        return parameters.entrySet().stream().map(this::paramToString).collect(Collectors.joining(", "));
    }

    private LinkedHashMap<String, Object> findMatchingConstructor(Class<?> clz, Map<String, Object> parameters) {
        LinkedHashMap<String, Object> result = null;

        for(Constructor<?> c: clz.getDeclaredConstructors()) {
            LinkedHashMap<String, Object> candidate = matchConstructor(c, parameters);

            if (candidate != null) {
                if (result == null) {
                    result = candidate;
                } else {
                    throw new HardcodeException(clz.getName()
                            + ": more than one matching constructor for parameters: " + describeParameters(parameters));
                }
            }
        }
        if(result == null) {
            throw new HardcodeException(clz.getName()
                    + ": no matching constructor for parameters: " + describeParameters(parameters));
        }
        return result;
    }

    @Override
    public Optional<NodeDefinition> createNode(Object object, HardcodeConfiguration configuration) {
        Objects.requireNonNull(object);

        Class<?> clz = object.getClass();
        if(clz.isSynthetic())
            return Optional.empty();

        // we cannot handle non static member classes
        int mods = clz.getModifiers();
        if(clz.isMemberClass() && !Modifier.isStatic(mods) && !Modifier.isPrivate(mods))
            return Optional.empty();

        IntrospectionResult introspection = strategy.introspect(clz);
        Map<String, Object> memberValues = introspection.getMemberValues(object);

        LinkedHashMap<String, Object> cons = findMatchingConstructor(clz, memberValues);

        NodeDefImpl nodeDef = NodeDefImpl.ref(
                clz,
                clz.getSimpleName(),
                ((context, obj) -> getCode(clz, obj)));

        for(Map.Entry<String, Object> e: cons.entrySet()) {
            String name = e.getKey();
            nodeDef.addParameter(
                    new NamedParameter(name),
                    e.getValue(),
                    introspection.getMemberAnnotations(name));
        }

        return Optional.of(nodeDef);
    }

    @Override
    public int getOrdering() {
        return 100;
    }
}
