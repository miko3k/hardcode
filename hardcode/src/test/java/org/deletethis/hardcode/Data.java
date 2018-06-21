package org.deletethis.hardcode;

import org.junit.Test;

import java.io.Serializable;
import java.util.Objects;

public class Data implements Serializable {
    private final String foo;
    private final int bar;
    private final Long lng;

    public Data(String foo, int bar, Long lng) {
        this.foo = foo;
        this.bar = bar;
        this.lng = lng;
    }

    public String getFoo() {
        return foo;
    }

    public int getBar() {
        return bar;
    }

    public Long getLng() {
        return lng;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Data)) return false;
        Data data = (Data) o;
        return bar == data.bar &&
                Objects.equals(foo, data.foo) &&
                Objects.equals(lng, data.lng);
    }

    @Override
    public int hashCode() {

        return Objects.hash(foo, bar, lng);
    }
}
