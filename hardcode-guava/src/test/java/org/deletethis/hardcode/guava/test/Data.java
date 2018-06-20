package org.deletethis.hardcode.guava.test;

import org.junit.Test;

import java.io.Serializable;

public class Data implements Serializable {
    private final String foo;
    private final int bar;
    private final Long lng;

    public Data(String foo, int bar, Long lng) {
        this.foo = foo;
        this.bar = bar;
        this.lng = lng;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Data)) return false;

        Data data = (Data) o;

        if (bar != data.bar) return false;
        if (foo != null ? !foo.equals(data.foo) : data.foo != null) return false;
        return lng != null ? lng.equals(data.lng) : data.lng == null;
    }

    @Override
    public int hashCode() {
        int result = foo != null ? foo.hashCode() : 0;
        result = 31 * result + bar;
        result = 31 * result + (lng != null ? lng.hashCode() : 0);
        return result;
    }
}
