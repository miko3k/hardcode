package org.deletethis.hardcode.objects.nodes.introspection;

import org.deletethis.hardcode.HardcodeException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConstructorWrapper {
    private final Constructor<?> constructor;
    private final List<ParameterWrapper> parameters;

    public ConstructorWrapper(Constructor<?> constructor) {
        this.constructor = constructor;
        Parameter[] params = constructor.getParameters();

        List<ParameterWrapper> tmp = new ArrayList<>(params.length);
        for(Parameter p: params) {
            if(!p.isNamePresent())
                throw new HardcodeException(constructor.getDeclaringClass().getName() + ": constructor parameter names not present");

            String name = p.getName();
            Class<?> type = p.getType();

            tmp.add(new ParameterWrapper(name, type));
        }

        this.parameters = Collections.unmodifiableList(tmp);
    }

    public List<ParameterWrapper> getParameters() {
        return parameters;
    }
}
