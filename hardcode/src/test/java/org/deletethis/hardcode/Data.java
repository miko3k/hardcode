package org.deletethis.hardcode;

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

    public String getFoo() {
        return foo;
    }

    public int getBar() {
        return bar;
    }

    public Long getLng() {
        return lng;
    }
}
