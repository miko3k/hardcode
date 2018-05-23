package org.deletethis.hardcode.guava.test;

import org.junit.Test;

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
}
