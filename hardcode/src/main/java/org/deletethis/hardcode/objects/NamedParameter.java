package org.deletethis.hardcode.objects;

public class NamedParameter implements ParameterName {
    private final String name;

    public NamedParameter(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }
}
