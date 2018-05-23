package org.deletethis.hardcode;

public class ParameterWrapper {
    private final String name;
    private final Class<?> type;

    public ParameterWrapper(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }
}