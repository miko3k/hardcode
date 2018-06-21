package org.deletethis.hardcode;

import org.junit.Test;

import java.util.Objects;

public class Container<T> {
    private T value;

    public Container(T value) {
        this.value = value;
    }

    public Container() {
        this.value = null;
    }


    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Container)) return false;
        Container<?> container = (Container<?>) o;
        return Objects.equals(value, container.value);
    }

    @Override
    public int hashCode() {

        return Objects.hash(value);
    }
}
