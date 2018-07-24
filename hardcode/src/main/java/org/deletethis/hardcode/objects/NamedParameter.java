package org.deletethis.hardcode.objects;

import java.util.Objects;

public class NamedParameter implements ParameterName {
    private final String name;

    public NamedParameter(String name) {
        this.name = Objects.requireNonNull(name);
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NamedParameter that = (NamedParameter) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
