package org.deletethis.hardcode.objects.impl;

import org.deletethis.hardcode.objects.*;

import java.lang.annotation.Annotation;
import java.util.*;

public class NodeDefImpl implements NodeDefinition {
    private static class NodeParameterImpl implements NodeParameter {
        private final ParameterName parameterName;
        private final Object value;
        private final List<Annotation> annotations;

        private NodeParameterImpl(ParameterName parameterName, Object value, List<Annotation> annotations) {
            this.parameterName = parameterName;
            this.value = value;
            this.annotations = annotations;
        }

        public ParameterName getParameterName() {
            return parameterName;
        }

        public Object getValue() {
            return value;
        }

        public List<Annotation> getAnnotations() {
            return annotations;
        }
    }

    private final boolean valueBased;
    private final Class<?> type;
    private final String asString;
    private final CodeGenerator constructionStrategy;
    private final List<NodeParameter> parameters = new ArrayList<>();
    private final Set<Class<? extends Throwable>> fatalExceptions = new HashSet<>();

    private NodeDefImpl(Class<?> type, String asString, CodeGenerator constructionStrategy, boolean valueBased) {
        this.type = type;
        this.asString = asString;
        this.constructionStrategy = constructionStrategy;
        this.valueBased = valueBased;
    }

    public static NodeDefImpl value(Class<?> type, String asString, CodeGenerator constructionStrategy) {
        return new NodeDefImpl(type, asString, constructionStrategy, true);
    }

    public static NodeDefImpl ref(Class<?> type, String asString, CodeGenerator constructionStrategy) {
        return new NodeDefImpl(type, asString, constructionStrategy, false);
    }

    public void addParameter(ParameterName parameterName, Object value, List<Annotation> annotations) {
        parameters.add(new NodeParameterImpl(parameterName, value, annotations));
    }

    public void addParameter(ParameterName parameterName, Object value) {
        parameters.add(new NodeParameterImpl(parameterName, value, Collections.emptyList()));
    }


    public void addFatalException(Class<? extends Throwable> exceptionClass) {
        fatalExceptions.add(Objects.requireNonNull(exceptionClass));
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public CodeGenerator getConstructionStrategy() {
        return constructionStrategy;
    }

    @Override
    public List<NodeParameter> getParameters() {
        return parameters;
    }

    @Override
    public Collection<Class<? extends Throwable>> getFatalExceptions() {
        return fatalExceptions;
    }

    @Override
    public String toString() {
        return asString;
    }

    @Override
    public boolean isValueBased() {
        return valueBased;
    }
}
