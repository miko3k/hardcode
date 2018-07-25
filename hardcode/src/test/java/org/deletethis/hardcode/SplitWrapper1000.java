package org.deletethis.hardcode;

import java.io.Serializable;

public class SplitWrapper1000 implements Serializable {
    @HardcodeSplit(1000)
    private Object payload;

    public SplitWrapper1000(Object payload) {
        this.payload = payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SplitWrapper1000)) return false;

        SplitWrapper1000 splitMap = (SplitWrapper1000) o;

        return payload.equals(splitMap.payload);
    }

    @Override
    public int hashCode() {
        return payload.hashCode();
    }
}
