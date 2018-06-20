package org.deletethis.hardcode.guava;

import org.deletethis.hardcode.HardcodeSplit;

import java.io.Serializable;

public class SplitWrapper2 implements Serializable {
    @HardcodeSplit(2)
    private Object payload;

    public SplitWrapper2(Object payload) {
        this.payload = payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SplitWrapper2)) return false;

        SplitWrapper2 splitMap = (SplitWrapper2) o;

        return payload.equals(splitMap.payload);
    }

    @Override
    public int hashCode() {
        return payload.hashCode();
    }
}
